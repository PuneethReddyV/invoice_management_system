package com.tellius.invoicemanagement.services

import java.time.LocalDate
import java.util.UUID

import com.google.inject.Inject
import com.tellius.invoicemanagement.entities.eums.{Due, InvoiceStatus}
import com.tellius.invoicemanagement.entities.readers.{
  InvoiceQueryParameters,
  UpdateNotifications
}
import com.tellius.invoicemanagement.entities.writes
import com.tellius.invoicemanagement.entities.writes._
import com.tellius.invoicemanagement.exception.RecordNotFound
import com.tellius.invoicemanagement.persistance.records._
import com.tellius.invoicemanagement.persistance.repository._

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}

trait NotificationsService {

  def updateNotifications(
    updateNotifications: UpdateNotifications
  ): Future[UpdateNotifications]

  def getCustomerInvoices(
    customerUuid: UUID,
    invoiceQueryParameters: InvoiceQueryParameters
  ): Future[CustomerInvoices]

  def getBusinessInvoices(
    businessUuid: UUID,
    invoiceQueryParameters: InvoiceQueryParameters
  ): Future[BusinessInvoices]

}

class NotificationsServiceImpl @Inject()(
  businessRepo: BusinessRepository,
  customerRepo: CustomerRepository,
  itemsRepo: ItemsRepository,
  itemsToInvoiceRepo: ItemsToInvoiceRepository,
  businessToCustomerRepo: BusinessToCustomerRepository,
  invoiceRepo: InvoiceRepository
) extends NotificationsService {

  implicit val ec: ExecutionContextExecutor =
    ExecutionContext.fromExecutor(new java.util.concurrent.ForkJoinPool())

  override def updateNotifications(
    updateNotifications: UpdateNotifications
  ): Future[UpdateNotifications] = {
    for {
      resultOpt <- businessToCustomerRepo.findByBusinessUuidAndCustomerUuid(
        updateNotifications.businessUuid,
        updateNotifications.customerUuid
      )
    } yield {
      resultOpt match {
        case Some(result)
            if result.remainderEnabled == updateNotifications.isEnabled =>
          Future(updateNotifications)
        case Some(result) =>
          businessToCustomerRepo
            .update(
              result.copy(remainderEnabled = updateNotifications.isEnabled)
            )
            .map(_ => updateNotifications)
        case _ =>
          throw RecordNotFound(s"Business and customer are not associated")
      }
    }
  }.flatten

  override def getCustomerInvoices(
    customerUuid: UUID,
    invoiceQueryParameters: InvoiceQueryParameters
  ): Future[CustomerInvoices] = {
    customerRepo.findByUuid(customerUuid).flatMap {
      case Some(customerRecord) =>
        createCustomerNotificationResponse(
          customerRecord,
          invoiceQueryParameters
        )
      case _ =>
        Future.failed(RecordNotFound(s"Invalid Customer UUID $customerUuid"))
    }
  }

  override def getBusinessInvoices(
    businessUuid: UUID,
    invoiceQueryParameters: InvoiceQueryParameters
  ): Future[BusinessInvoices] = {
    businessRepo.findByUuid(businessUuid).flatMap {
      case Some(businessRecord) =>
        createBusinessNotificationResponse(
          businessRecord,
          invoiceQueryParameters
        )
      case _ =>
        Future.failed(RecordNotFound(s"Invalid Business UUID $businessUuid"))
    }
  }

  private def getValidDatedRecords(
    data: Seq[InvoiceRecord],
    invoiceQueryParameters: InvoiceQueryParameters
  ): Seq[InvoiceRecord] = {
    invoiceQueryParameters.due match {
      case Due.On =>
        data.filter(
          _.dueDate.fold(false)(
            e => e == LocalDate.now().plusDays(invoiceQueryParameters.interval)
          )
        )
      case Due.WithIn if invoiceQueryParameters.interval > 0 =>
        data.filter(
          _.dueDate.fold(false)(
            e => //include today's data as well
              e.isAfter(LocalDate.now().minusDays(1)) && e.isBefore(
                LocalDate.now().plusDays(invoiceQueryParameters.interval)
            )
          )
        )
      case _ =>
        data.filter(
          _.dueDate.fold(false)(
            e => //include today's data as well
              e.isBefore(LocalDate.now().plusDays(1)) && e.isAfter(
                LocalDate.now().plusDays(invoiceQueryParameters.interval)
            )
          )
        )
    }
  }

  private def createCustomerNotificationResponse(
    customerRecord: CustomerRecord,
    invoiceQueryParameters: InvoiceQueryParameters
  ) = {
    for {
      rawInvoicesByCustomer <- invoiceRepo.findByCustomerUuidPaymentStatus(
        customerRecord.uuid,
        invoiceQueryParameters.paymentStatus
      )
      notifyToBusinessUuidRaw <- businessToCustomerRepo
        .findByCustomerUuidAndNotification(
          customerRecord.uuid,
          invoiceQueryParameters.isValid
        )
      notificationsEnabledData = rawInvoicesByCustomer.filter(
        e =>
          notifyToBusinessUuidRaw.map(_.businessUuid).contains(e.businessUuid)
      )
      invoicesByCustomer = getValidDatedRecords(
        notificationsEnabledData,
        invoiceQueryParameters
      )
      businessData <- businessRepo
        .findByUuids(invoicesByCustomer.map(_.businessUuid))
      itemsToInvoice <- itemsToInvoiceRepo
        .findByInvoiceUuids(invoicesByCustomer.map(_.uuid))
      items <- itemsRepo
        .findByUuids(itemsToInvoice.map(_.itemsUuid).distinct)
    } yield {
      CustomerInvoices(
        customerRecord.uuid,
        customerRecord.name,
        customerRecord.contactNumber,
        customerRecord.emailId,
        createCustomerNotificationsResponse(
          businessData,
          items,
          itemsToInvoice,
          invoicesByCustomer,
          invoiceQueryParameters.interval
        ).distinct
      )
    }
  }

  private def createBusinessNotificationResponse(
    businessRecord: BusinessRecord,
    invoiceQueryParameters: InvoiceQueryParameters
  ) = {
    for {
      rawInvoicesByBusiness <- invoiceRepo.findByBusinessUuidPaymentStatus(
        businessRecord.uuid,
        invoiceQueryParameters.paymentStatus
      )
      notifyToBusinessUuidRaw <- businessToCustomerRepo
        .findByBusinessUuidAndNotification(
          businessRecord.uuid,
          invoiceQueryParameters.isValid
        )
      notificationsEnabledData = rawInvoicesByBusiness.filter(
        e =>
          notifyToBusinessUuidRaw.map(_.customerUuid).contains(e.customerUUID)
      )
      invoicesByBusiness = getValidDatedRecords(
        notificationsEnabledData,
        invoiceQueryParameters
      )
      customerData <- customerRepo
        .findByUuids(invoicesByBusiness.map(_.customerUUID))
      itemsToInvoice <- itemsToInvoiceRepo
        .findByInvoiceUuids(invoicesByBusiness.map(_.uuid))
      items <- itemsRepo
        .findByUuids(itemsToInvoice.map(_.itemsUuid).distinct)
    } yield {
      writes.BusinessInvoices(
        businessRecord.uuid,
        businessRecord.name,
        createBusinessToCustomerData(
          customerData,
          items,
          itemsToInvoice,
          invoicesByBusiness,
          invoiceQueryParameters.interval
        ).distinct
      )
    }
  }

  private def createCustomerNotificationsResponse(
    businessRecords: Seq[BusinessRecord],
    itemRecords: Seq[ItemRecord],
    itemsToInvoices: Seq[ItemsToInvoiceRecord],
    invoiceRecords: Seq[InvoiceRecord],
    interval: Int
  ): Seq[BusinessList] = {
    invoiceRecords.flatMap(invoiceRecord => {
      businessRecords
        .find(_.uuid == invoiceRecord.businessUuid)
        .map(
          e =>
            BusinessList(
              e.uuid,
              e.name,
              createInvoices(
                interval,
                itemRecords,
                itemsToInvoices,
                invoiceRecords.filter(_.businessUuid == e.uuid)
              )
          )
        )

    })
  }

  private def createInvoices(
    interval: Int,
    itemRecords: Seq[ItemRecord],
    itemsToInvoices: Seq[ItemsToInvoiceRecord],
    invoiceRecords: Seq[InvoiceRecord]
  ): Seq[InvoicesList] = {

    invoiceRecords.map(invoiceRecord => {
      val items: Seq[Items] = itemsToInvoices
        .filter(_.invoiceUuid == invoiceRecord.uuid)
        .flatMap(itemsToInvoice => {
          itemRecords
            .find(_.uuid == itemsToInvoice.itemsUuid)
            .map(e => Items(e.uuid, e.name, e.price, itemsToInvoice.quantity))
        })
      InvoicesList(
        invoiceRecord.uuid,
        invoiceRecord.amount,
        createMessage(
          invoiceRecord.paymentStatus,
          invoiceRecord.dueDate,
          interval
        ),
        invoiceRecord.paymentStatus,
        invoiceRecord.dueDate,
        items
      )
    })
  }

  private def createBusinessToCustomerData(
    customersData: Seq[CustomerRecord],
    itemRecords: Seq[ItemRecord],
    itemsToInvoices: Seq[ItemsToInvoiceRecord],
    invoiceRecords: Seq[InvoiceRecord],
    interval: Int
  ): Seq[CustomersList] = {
    invoiceRecords.flatMap(invoiceRecord => {
      customersData
        .find(_.uuid == invoiceRecord.customerUUID)
        .map(
          e =>
            CustomersList(
              e.uuid,
              e.name,
              e.contactNumber,
              e.emailId,
              createInvoices(
                interval,
                itemRecords,
                itemsToInvoices,
                invoiceRecords.filter(_.customerUUID == e.uuid)
              )
          )
        )
    })
  }

  private def createMessage(paymentStatus: InvoiceStatus,
                            dueDateOpt: Option[LocalDate],
                            interval: Int) = {
    val dueByInWords =
      if (interval > 0) { "before due date by" } else { "after due date by" }
    (paymentStatus, dueDateOpt) match {
      case (InvoiceStatus.Paid, None) => "Send SMS/EMAIL paid"
      case (InvoiceStatus.Paid, Some(date)) =>
        s"Send SMS/EMAIL paid on $date"
      case (InvoiceStatus.UnPaid, Some(date)) =>
        import java.time.temporal.ChronoUnit
        val days = ChronoUnit.DAYS.between(date, LocalDate.now())
        s"Send SMS/EMAIL remainder $dueByInWords ${Math.abs(days)} days."
      case (InvoiceStatus.UnPaid, None) =>
        s"Send SMS/EMAIL remainder overDue as on this day."
    }

  }
}
