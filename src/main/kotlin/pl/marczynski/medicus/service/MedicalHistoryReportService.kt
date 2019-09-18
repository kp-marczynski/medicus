package pl.marczynski.medicus.service

import com.lowagie.text.*
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfWriter

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pl.marczynski.medicus.domain.*
import pl.marczynski.medicus.repository.*

import pl.marczynski.medicus.security.getCurrentUserLogin
import java.awt.Color
import java.io.ByteArrayOutputStream
import java.sql.Timestamp
import java.time.LocalDate

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
    private val appointmentRepository: AppointmentRepository
) {

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
            tables.addAll(getTreatmentTables())

            val para = Paragraph("Medical History Report - " + getCurrentUserLogin().orElse("") + " - " + Timestamp(System.currentTimeMillis()))
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
        val examinationPackagesGroups = examinationPackageRepository.findAll().groupBy { it.date!! }
        examinationPackagesGroups.forEach { elem ->
            val table = PdfPTable(2)
            table.setWidths(floatArrayOf(1f, 3f))
            arrayOf("Title", "Examinations").forEach { table.addCell(getHeaderCell(it)) }
            elem.value.forEach { examinationPackage ->
                table.addCell(PdfPCell(Phrase(examinationPackage.title)))

                val examinationTable = PdfPTable(2)
                examinationPackage.examinations?.forEach {
                    examinationTable.addCell(PdfPCell(Phrase(it.examinationType?.name)))
                    examinationTable.addCell(PdfPCell(Phrase(it.value.toString() + " " + it.examinationType?.unit)))
                }
                table.addCell(PdfPCell(examinationTable))
            }
            tables.add(Pair(elem.key, wrapTable(table, elem.key, "Examination Package")))
        }

        return tables
    }

    private fun getSymptomsTables(): MutableCollection<Pair<LocalDate, PdfPTable>> {
        val tables: MutableCollection<Pair<LocalDate, PdfPTable>> = mutableListOf()
        val symptomGroups = symptomRepository.findAll().groupBy { it.startDate!! }
        symptomGroups.forEach { elem ->
            val table = PdfPTable(3)
            table.setWidths(floatArrayOf(1f, 1f, 2f))
            arrayOf("Start Date", "End Date", "Description").forEach { table.addCell(getHeaderCell(it)) }
            elem.value.forEach { symptom ->
                table.addCell(PdfPCell(Phrase(symptom.startDate.toString())))
                table.addCell(PdfPCell(Phrase(symptom.endDate?.toString() ?: "")))
                table.addCell(PdfPCell(Phrase(symptom.description ?: "")))
            }
            tables.add(Pair(elem.key, wrapTable(table, elem.key, "Symptom")))
        }

        return tables
    }

    private fun getTreatmentTables(): MutableCollection<Pair<LocalDate, PdfPTable>> {
        val tables: MutableCollection<Pair<LocalDate, PdfPTable>> = mutableListOf()
        val treatmentGroups = treatmentRepository.findAllWithEagerRelationships()
        treatmentGroups.forEach { treatment ->
            val table = PdfPTable(2)
            table.setWidths(floatArrayOf(1f, 2f))

            table.addCell(getHeaderCell("Start Date"))
            table.addCell(PdfPCell(Phrase(treatment.startDate.toString())))

            table.addCell(getHeaderCell("End Date"))
            table.addCell(PdfPCell(Phrase(treatment.endDate?.toString() ?: "")))

            table.addCell(getHeaderCell("Visited Doctors"))
            val doctorsTable = PdfPTable(1)
            treatment.visitedDoctors?.forEach { doctorsTable.addCell(getBorderlessCell(it.specialization + " - " + it.name)) }
            table.addCell(doctorsTable)

            table.addCell(getHeaderCell("Description"))
            table.addCell(PdfPCell(Phrase(treatment.description ?: "")))

            table.addCell(getHeaderCell("Medicines"))
            val medicinesTable = PdfPTable(1)
            treatment.medicines?.forEach { medicinesTable.addCell(getBorderlessCell(it.name!!)) }
            table.addCell(medicinesTable)

            tables.add(Pair(treatment.startDate!!, wrapTable(table, treatment.startDate!!, "Treatment")))
        }

        return tables
    }

    private fun wrapTable(table: PdfPTable, date: LocalDate, recordType: String): PdfPTable {
        val tableContainer = PdfPTable(3)
        tableContainer.setWidths(floatArrayOf(1f, 1f, 5f))
        tableContainer.addCell(PdfPCell(Phrase(date.toString())))
        tableContainer.addCell(PdfPCell(Phrase(recordType)))
        val cell = PdfPCell(table)
        cell.setPadding(10f)
        tableContainer.addCell(cell)
        tableContainer.widthPercentage = 100f
        return tableContainer
    }

    private fun getHeaderCell(text: String): PdfPCell {
        val header = PdfPCell(Phrase(text))
        header.backgroundColor = Color.LIGHT_GRAY
        header.horizontalAlignment = Element.ALIGN_CENTER
        return header
    }

    private fun getBorderlessCell(text: String): PdfPCell {
        val cell = PdfPCell(Phrase(text))
        cell.borderWidth = 0f
        return cell
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
