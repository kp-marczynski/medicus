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
        val defaultFont = Font(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1250, BaseFont.EMBEDDED), 11f)
        var lang = "en"
    }

    private val log = LoggerFactory.getLogger(javaClass)

    fun getReport(language: String?): MedicalHistoryReport {
        lang = language ?: "en"
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
            table.addCell(getPaddedCell(examinationPackage.title))

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
                examinationTable.addCell(getPaddedCell(it.examinationType?.name))
                examinationTable.addCell(getPaddedCell(it.value.toString() + " " + it.examinationType?.unit))
                examinationTable.addCell(getPaddedCell(it.examinationType?.minGoodValue.toString() + " - " + it.examinationType?.maxGoodValue.toString()))
            }
            table.addCell(PdfPCell(examinationTable))

            tables.add(Pair(examinationPackage.date!!, wrapTable(table, examinationPackage.date!!, getTranslation("report.examinationPackage.header"))))
        }

        return tables
    }

    private fun getSymptomsTables(): MutableCollection<Pair<LocalDate, PdfPTable>> {
        val tables: MutableCollection<Pair<LocalDate, PdfPTable>> = mutableListOf()
        val symptomGroups = symptomRepository.findAll()
        symptomGroups.forEach { symptom ->
            val table = PdfPTable(2)
            table.setWidths(floatArrayOf(1f, 3f))

            table.addCell(getHeaderCell(getTranslation("report.symptom.startDate")))
            table.addCell(getPaddedCell(symptom.startDate.toString()))

            table.addCell(getHeaderCell(getTranslation("report.symptom.endDate")))
            table.addCell(getPaddedCell(symptom.endDate?.toString()))

            table.addCell(getHeaderCell(getTranslation("report.symptom.description")))
            table.addCell(getPaddedCell(symptom.description))

            tables.add(Pair(symptom.startDate!!, wrapTable(table, symptom.startDate!!, getTranslation("report.symptom.header"))))
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
            table.addCell(getPaddedCell(treatment.title))

            table.addCell(getHeaderCell(getTranslation("report.treatment.startDate")))
            table.addCell(getPaddedCell(treatment.startDate.toString()))

            table.addCell(getHeaderCell(getTranslation("report.treatment.endDate")))
            table.addCell(getPaddedCell(treatment.endDate?.toString()))

            table.addCell(getHeaderCell(getTranslation("report.treatment.visitedDoctors")))
            table.addCell(createTabularList(treatment.visitedDoctors?.map { it.specialization + " - " + it.name }))

            table.addCell(getHeaderCell(getTranslation("report.treatment.description")))
            table.addCell(getPaddedCell(treatment.description))

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
            table.addCell(getPaddedCell(procedure.title))

            table.addCell(getHeaderCell(getTranslation("report.procedure.visitedDoctors")))
            table.addCell(createTabularList(procedure.visitedDoctors?.map { it.specialization + " - " + it.name }))

            table.addCell(getHeaderCell(getTranslation("report.procedure.description")))
            table.addCell(getPaddedCell(procedure.description))

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
            table.addCell(getPaddedCell(appointment.title))

            table.addCell(getHeaderCell(getTranslation("report.appointment.visitedDoctors")))
            table.addCell(createTabularList(appointment.visitedDoctors?.map { it.specialization + " - " + it.name }))

            table.addCell(getHeaderCell(getTranslation("report.appointment.description")))
            table.addCell(getPaddedCell(appointment.description))

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
        tableContainer.addCell(getCenteredCell(date.toString()))
        tableContainer.addCell(getCenteredCell(recordType))
        val cell = PdfPCell(table)
        cell.setPadding(10f)
        tableContainer.addCell(cell)
        tableContainer.widthPercentage = 100f
        return tableContainer
    }

    private fun getHeaderCell(text: String?): PdfPCell {
        val header = getCenteredCell(text)
        header.backgroundColor = Color.LIGHT_GRAY
        return header
    }

    private fun getPaddedCell(text: String?): PdfPCell {
        val cell = PdfPCell(createPhrase(text))
        cell.setPadding(5f)
        return cell
    }

    private fun getCenteredCell(text: String?): PdfPCell {
        val cell = getPaddedCell(text)
        cell.horizontalAlignment = Element.ALIGN_CENTER
        return cell
    }

    private fun getBorderlessCell(text: String): PdfPCell {
        val cell = getPaddedCell(text)
        cell.borderWidth = 0f
        return cell
    }

    private fun getTranslation(key: String): String {
        val locale = Locale.forLanguageTag(lang)
        return messageSource.getMessage(key, null, locale)
    }

    private fun createPhrase(text: String?): Phrase {
        return Phrase(text, defaultFont)
    }
}

// internal enum class RecordType(val name: String, val color: Color) {
//    SYMPTOM, PROCEDURE, TREATMENT, EXAMINATION_PACKAGE, APPOINTMENT
// }
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
