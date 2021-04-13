package com.tellius.invoicemanagement.services

import java.time.LocalDate
import java.util.UUID

import com.google.inject.Inject
import com.tellius.invoicemanagement.entities.eums.InvoiceStatus
import com.tellius.invoicemanagement.entities.readers.CreateNewInvoice
import com.tellius.invoicemanagement.entities.writes._
import com.tellius.invoicemanagement.exception.{
  CreateFailedException,
  RecordNotFound
}
import com.tellius.invoicemanagement.persistance.records.{
  BusinessRecord,
  CustomerRecord,
  ItemRecord
}
import com.tellius.invoicemanagement.persistance.repository._
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}

trait InvoiceService {
  def createNewInvoicesResource(
    createNewInvoice: CreateNewInvoice
  ): Future[CreatedResource]

}

class InvoiceServiceImpl @Inject()(businessRepo: BusinessRepository,
                                   customerRepo: CustomerRepository,
                                   itemsRepo: ItemsRepository,
                                   invoiceRepo: InvoiceRepository)
    extends InvoiceService
    with StrictLogging {

  implicit val ec: ExecutionContextExecutor =
    ExecutionContext.fromExecutor(new java.util.concurrent.ForkJoinPool())

  override def createNewInvoicesResource(
    createNewInvoice: CreateNewInvoice
  ): Future[CreatedResource] = {
    for {
      businessRecordOpt <- businessRepo.findByUuid(
        createNewInvoice.businessUuid
      )
      customerRecordOpt <- customerRepo.findByUuid(
        createNewInvoice.customerUuid
      )
      itemRecords <- itemsRepo.findByUuids(
        createNewInvoice.items.map(_.itemUuid)
      )
      _ = isValidateBusinessAndCustomerRecords(
        createNewInvoice,
        customerRecordOpt,
        businessRecordOpt
      )
      _ = validateItemsUuids(
        createNewInvoice.items.map(_.itemUuid),
        itemRecords.map(_.uuid)
      )
      _ = isValidPostBody(createNewInvoice, itemRecords)
      createdData <- invoiceRepo.save(createNewInvoice)
    } yield {
      createdData match {
        case Some(uuid) => CreatedResource(uuid)
        case _ =>
          throw CreateFailedException("Could not create invoice resource.")
      }
    }
  }

  private def isValidateBusinessAndCustomerRecords(
    createNewInvoice: CreateNewInvoice,
    customerRecordOpt: Option[CustomerRecord],
    businessRecordOpt: Option[BusinessRecord]
  ): Boolean = {
    (customerRecordOpt, businessRecordOpt) match {
      case (None, None) =>
        throw RecordNotFound(
          s"Business record ${createNewInvoice.businessUuid} not found. " +
            s"\nCustomer record ${createNewInvoice.customerUuid} not found.  "
        )
      case (None, _) =>
        throw RecordNotFound(
          s"Business record ${createNewInvoice.customerUuid} not found."
        )
      case (_, None) =>
        throw RecordNotFound(
          s"Customer record ${createNewInvoice.businessUuid} not found."
        )
      case _ => true
    }
  }

  private def validateItemsUuids(providedItemUuids: Seq[UUID],
                                 availableItemUuids: Seq[UUID]): Boolean = {
    val invalidUuids = providedItemUuids.diff(availableItemUuids)
    if (invalidUuids.isEmpty) {
      true
    } else {
      throw RecordNotFound(
        s"Items record ${invalidUuids.mkString("[", ", ", "]")} not found."
      )
    }
  }

  private def isValidPostBody(createNewInvoice: CreateNewInvoice,
                              itemRecords: Seq[ItemRecord]): Boolean = {
    if (createNewInvoice.paymentStatus != InvoiceStatus.UnPaid && createNewInvoice.dueDate
          .fold(false)(date => LocalDate.now().isBefore(date))) {
      logger.error(
        s"when due date=${createNewInvoice.dueDate} is on future date payment should be pending"
      )
      throw CreateFailedException(
        "when due date is on future date payment should be pending"
      )
    }
    val calculatedSum = itemRecords
      .map(
        item =>
          BigDecimal(
            item.price * createNewInvoice.items
              .find(_.itemUuid == item.uuid)
              .fold(0.0)(reqQty => reqQty.quantity)
          ).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
      )
      .fold(0.0)((item, sum) => item + sum)

    if (calculatedSum != createNewInvoice.amount) {
      logger.error(s"$calculatedSum is not equal to ${createNewInvoice.amount}")
      throw CreateFailedException(
        s"$calculatedSum is not equal to ${createNewInvoice.amount}"
      )
    }
    true
  }

}
