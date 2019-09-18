package pl.marczynski.medicus.service

import com.lowagie.text.*
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfWriter
import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pl.marczynski.medicus.domain.MedicalHistoryReport
import pl.marczynski.medicus.repository.*
import pl.marczynski.medicus.security.getCurrentUserLogin
import java.awt.Color
import java.io.ByteArrayOutputStream
import java.sql.Timestamp
import java.time.LocalDate
import java.util.*
import com.lowagie.text.pdf.BaseFont
import com.lowagie.text.Font

/**
 * Service class for managing users.
 */
@Service
@Transactional
class MedicalHistoryReportService(
    private val userRepository: UserRepository,
    private val examinationPackageRepository: ExaminationPackageRepository,
    private val symptomRepository: SymptomRepository,
    private val treatmentRepository: TreatmentRepository,
    private val procedureRepository: ProcedureRepository,
    private val appointmentRepository: AppointmentRepository,
    private val messageSource: MessageSource
) {
    companion object {
        val defaultFont = Font(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1250, BaseFont.EMBEDDED), 12f)
    }

    private val log = LoggerFactory.getLogger(javaClass)

    fun getReport(): MedicalHistoryReport {
        val document = Document()
        val out = ByteArrayOutputStream()

        try {
            PdfWriter.getInstance(document, out)
            document.open()

            val tables: MutableCollection<Pair<LocalDate, PdfPTable>> = mutableListOf()
            tables.addAll(getExaminationPackagesTables())
            tables.addAll(getSymptomsTables())
            tables.addAll(getTreatmentsTables())
            tables.addAll(getProceduresTables())
            tables.addAll(getAppointmentsTables())

            val para = Paragraph(getTranslation("report.header") + " - " + getCurrentUserLogin().orElse("") + " - " + Timestamp(System.currentTimeMillis()), defaultFont)
            para.alignment = Element.ALIGN_CENTER
            document.add(para)
            document.add(Chunk.NEWLINE)

            tables.sortedWith(compareBy { it.first }).forEach {
                document.add(it.second)
            }

            document.close()
        } catch (e: DocumentException) {
            LoggerFactory.getLogger(this::class.java).error(e.toString())
        }

        return MedicalHistoryReport(getCurrentUserLogin().orElse(null), out.toByteArray())
    }

    private fun getExaminationPackagesTables(): MutableCollection<Pair<LocalDate, PdfPTable>> {
        val tables: MutableCollection<Pair<LocalDate, PdfPTable>> = mutableListOf()
        val examinationPackagesGroups = examinationPackageRepository.findAll()
        examinationPackagesGroups.forEach { examinationPackage ->
            val table = PdfPTable(2)
            table.setWidths(floatArrayOf(1f, 3f))
            table.addCell(getHeaderCell(getTranslation("report.examinationPackage.title")))
            table.addCell(PdfPCell(createPhrase(examinationPackage.title)))

            table.addCell(getHeaderCell(getTranslation("report.examinationPackage.visitedDoctors")))
            table.addCell(createTabularList(examinationPackage.visitedDoctors?.map { it.specialization + " - " + it.name }))

            table.addCell(getHeaderCell(getTranslation("report.examinationPackage.examinations")))
            val examinationTable = PdfPTable(3)
            arrayOf(
                getTranslation("report.examinationPackage.examinations.name"),
                getTranslation("report.examinationPackage.examinations.value"),
                getTranslation("report.examinationPackage.examinations.reference")
            ).forEach { examinationTable.addCell(getHeaderCell(it)) }
            examinationPackage.examinations?.forEach {
                examinationTable.addCell(PdfPCell(createPhrase(it.examinationType?.name)))
                examinationTable.addCell(PdfPCell(createPhrase(it.value.toString() + " " + it.examinationType?.unit)))
                examinationTable.addCell(PdfPCell(createPhrase(it.examinationType?.minValue.toString() + " - " + it.examinationType?.maxValue.toString())))
            }
            table.addCell(PdfPCell(examinationTable))

            tables.add(Pair(examinationPackage.date!!, wrapTable(table, examinationPackage.date!!, getTranslation("report.examinationPackage.header"))))
        }

        return tables
    }

    private fun getSymptomsTables(): MutableCollection<Pair<LocalDate, PdfPTable>> {
        val tables: MutableCollection<Pair<LocalDate, PdfPTable>> = mutableListOf()
        val symptomGroups = symptomRepository.findAll().groupBy { it.startDate!! }
        symptomGroups.forEach { elem ->
            val table = PdfPTable(3)
            table.setWidths(floatArrayOf(1f, 1f, 2f))
            arrayOf(
                getTranslation("report.symptom.startDate"),
                getTranslation("report.symptom.endDate"),
                getTranslation("report.symptom.description")
            ).forEach { table.addCell(getHeaderCell(it)) }
            elem.value.forEach { symptom ->
                table.addCell(PdfPCell(createPhrase(symptom.startDate.toString())))
                table.addCell(PdfPCell(createPhrase(symptom.endDate?.toString())))
                table.addCell(PdfPCell(createPhrase(symptom.description)))
            }
            tables.add(Pair(elem.key, wrapTable(table, elem.key, getTranslation("report.symptom.header"))))
        }

        return tables
    }

    private fun getTreatmentsTables(): MutableCollection<Pair<LocalDate, PdfPTable>> {
        val tables: MutableCollection<Pair<LocalDate, PdfPTable>> = mutableListOf()
        val treatmentGroups = treatmentRepository.findAllWithEagerRelationships()
        treatmentGroups.forEach { treatment ->
            val table = PdfPTable(2)
            table.setWidths(floatArrayOf(1f, 3f))

            table.addCell(getHeaderCell(getTranslation("report.treatment.title")))
            table.addCell(PdfPCell()) // todo add title to treatment domain model

            table.addCell(getHeaderCell(getTranslation("report.treatment.startDate")))
            table.addCell(PdfPCell(createPhrase(treatment.startDate.toString())))

            table.addCell(getHeaderCell(getTranslation("report.treatment.endDate")))
            table.addCell(PdfPCell(createPhrase(treatment.endDate?.toString())))

            table.addCell(getHeaderCell(getTranslation("report.treatment.visitedDoctors")))
            table.addCell(createTabularList(treatment.visitedDoctors?.map { it.specialization + " - " + it.name }))

            table.addCell(getHeaderCell(getTranslation("report.treatment.description")))
            table.addCell(PdfPCell(createPhrase(treatment.description)))

            table.addCell(getHeaderCell(getTranslation("report.treatment.medicines")))
            table.addCell(createTabularList(treatment.medicines?.map { it.name!! }))

            tables.add(Pair(treatment.startDate!!, wrapTable(table, treatment.startDate!!, getTranslation("report.treatment.header"))))
        }

        return tables
    }

    private fun getProceduresTables(): MutableCollection<Pair<LocalDate, PdfPTable>> {
        val tables: MutableCollection<Pair<LocalDate, PdfPTable>> = mutableListOf()
        val proceduresGroups = procedureRepository.findAllWithEagerRelationships()
        proceduresGroups.forEach { procedure ->
            val table = PdfPTable(2)
            table.setWidths(floatArrayOf(1f, 3f))

            table.addCell(getHeaderCell(getTranslation("report.procedure.title")))
            table.addCell(PdfPCell(createPhrase(procedure.title)))

            table.addCell(getHeaderCell(getTranslation("report.procedure.visitedDoctors")))
            table.addCell(createTabularList(procedure.visitedDoctors?.map { it.specialization + " - " + it.name }))

            table.addCell(getHeaderCell(getTranslation("report.procedure.description")))
            table.addCell(PdfPCell(createPhrase(procedure.description)))

            tables.add(Pair(procedure.date!!, wrapTable(table, procedure.date!!, getTranslation("report.procedure.header"))))
        }

        return tables
    }

    private fun getAppointmentsTables(): MutableCollection<Pair<LocalDate, PdfPTable>> {
        val tables: MutableCollection<Pair<LocalDate, PdfPTable>> = mutableListOf()
        val appointmentsGroups = appointmentRepository.findAllWithEagerRelationships()
        appointmentsGroups.forEach { appointment ->
            val table = PdfPTable(2)
            table.setWidths(floatArrayOf(1f, 3f))

            table.addCell(getHeaderCell(getTranslation("report.appointment.title")))
            table.addCell(PdfPCell(createPhrase(appointment.appointmentType))) // todo change appointmentType to title

            table.addCell(getHeaderCell(getTranslation("report.appointment.visitedDoctors")))
            table.addCell(createTabularList(appointment.visitedDoctors?.map { it.specialization + " - " + it.name }))

            table.addCell(getHeaderCell(getTranslation("report.appointment.description")))
            table.addCell(PdfPCell(createPhrase(appointment.description)))

            table.addCell(getHeaderCell(getTranslation("report.appointment.symptoms")))
            table.addCell(createTabularList(appointment.symptoms?.map { it.description!! }))
            table.addCell(getHeaderCell(getTranslation("report.appointment.treatments")))
            table.addCell(createTabularList(appointment.treatments?.map { it.startDate.toString() + " - " + it.description }))
            table.addCell(getHeaderCell(getTranslation("report.appointment.examinationPackages")))
            table.addCell(createTabularList(appointment.examinationPackages?.map { it.date.toString() + " - " + it.title }))
            table.addCell(getHeaderCell(getTranslation("report.appointment.procedures")))
            table.addCell(createTabularList(appointment.procedures?.map { it.date.toString() + " - " + it.title }))

            tables.add(Pair(appointment.date!!, wrapTable(table, appointment.date!!, getTranslation("report.appointment.header"))))
        }

        return tables
    }

    private fun createTabularList(source: List<String>?): PdfPTable {
        val resultTable = PdfPTable(1)
        source?.forEach { resultTable.addCell(getBorderlessCell(it)) }
        return resultTable
    }

    private fun wrapTable(table: PdfPTable, date: LocalDate, recordType: String): PdfPTable {
        val tableContainer = PdfPTable(3)
        tableContainer.setWidths(floatArrayOf(1f, 1f, 5f))
        tableContainer.addCell(PdfPCell(createPhrase(date.toString())))
        tableContainer.addCell(PdfPCell(createPhrase(recordType)))
        val cell = PdfPCell(table)
        cell.setPadding(10f)
        tableContainer.addCell(cell)
        tableContainer.widthPercentage = 100f
        return tableContainer
    }

    private fun getHeaderCell(text: String): PdfPCell {
        val header = PdfPCell(createPhrase(text))
        header.backgroundColor = Color.LIGHT_GRAY
        header.horizontalAlignment = Element.ALIGN_CENTER
        return header
    }

    private fun getBorderlessCell(text: String): PdfPCell {
        val cell = PdfPCell(createPhrase(text))
        cell.borderWidth = 0f
        return cell
    }

    private fun getTranslation(key: String): String {
        val locale = Locale.forLanguageTag(userRepository.getCurrentUserLanguage().orElse("en"))
        return messageSource.getMessage(key, null, locale)
    }

    private fun createPhrase(text: String?): Phrase {
        return Phrase(text, defaultFont)
    }
}

// internal class ParagraphBorder : PdfPageEventHelper() {
//    private var active = false
//
//    private var offset = 5f
//    private var startPosition: Float = 0.toFloat()
//
//    override fun onParagraph(
//        writer: PdfWriter,
//        document: Document,
//        paragraphPosition: Float
//    ) {
//        this.startPosition = paragraphPosition
//    }
//
//    override fun onParagraphEnd(
//        writer: PdfWriter,
//        document: Document,
//        paragraphPosition: Float
//    ) {
//        if (active) {
//            val cb = writer.directContentUnder
//            cb.rectangle(document.left(), paragraphPosition - offset,
//                document.right() - document.left(),
//                startPosition - paragraphPosition)
//            cb.setColorStroke(Color.LIGHT_GRAY)
//            cb.stroke()
//        }
//    }
//
//    fun setActive(active: Boolean) {
//        this.active = active
//    }
// }
