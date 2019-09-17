package pl.marczynski.medicus.service

import com.lowagie.text.*
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfWriter
import pl.marczynski.medicus.repository.UserRepository

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pl.marczynski.medicus.domain.*

import pl.marczynski.medicus.repository.ExaminationPackageRepository
import pl.marczynski.medicus.repository.SymptomRepository
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
    private val symptomRepository: SymptomRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun getReport(): MedicalHistoryReport {
        var document = Document()
        var out = ByteArrayOutputStream()

        try {
            PdfWriter.getInstance(document, out)
            document.open()

            // Add Text to PDF file ->
            val tables: MutableCollection<Pair<LocalDate, PdfPTable>> = mutableListOf()
            tables.addAll(getExaminationPackagesTables())
            tables.addAll(getSymptomsTables())

            val para = Paragraph("Medical History Report - " + getCurrentUserLogin().orElse("") + " - " + Timestamp(System.currentTimeMillis()))
            para.setAlignment(Element.ALIGN_CENTER)
            document.add(para)
            document.add(Chunk.NEWLINE)

            tables.sortedBy { it.first }
            tables.forEach {
                document.add(Paragraph(it.first.toString()))
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
        // Add PDF Table Header ->
        val examinationPackagesGroups = examinationPackageRepository.findAllWithoutAppointment().groupBy { it.date!! }
        for (elem in examinationPackagesGroups) {
            val table = PdfPTable(3)
            table.setWidths(floatArrayOf(1f, 1f, 3f))
            arrayOf("Date", "Title", "Examinations").forEach { table.addCell(getHeaderCell(it)) }
            for (examinationPackage in elem.value) {
                table.addCell(PdfPCell(Phrase(examinationPackage.date.toString())))
                table.addCell(PdfPCell(Phrase(examinationPackage.title)))

                val examinationTable = PdfPTable(2)
                examinationPackage.examinations?.forEach {
                    examinationTable.addCell(PdfPCell(Phrase(it.examinationType?.name)))
                    examinationTable.addCell(PdfPCell(Phrase(it.value.toString() + " " + it.examinationType?.unit)))
                }
                table.addCell(PdfPCell(examinationTable))
            }
            tables.add(Pair(elem.key, table))
        }

        return tables
    }

    private fun getSymptomsTables(): MutableCollection<Pair<LocalDate, PdfPTable>> {
        val tables: MutableCollection<Pair<LocalDate, PdfPTable>> = mutableListOf()
        // Add PDF Table Header ->
        val symptomGroups = symptomRepository.findAll().groupBy { it.startDate!! }
        for (elem in symptomGroups) {
            val table = PdfPTable(3)
            arrayOf("Start Date", "End Date", "Description").forEach { table.addCell(getHeaderCell(it)) }
            for (examinationPackage in elem.value) {
                table.addCell(PdfPCell(Phrase(examinationPackage.startDate.toString())))
                table.addCell(PdfPCell(Phrase(examinationPackage.endDate?.toString() ?: "")))
                table.addCell(PdfPCell(Phrase(examinationPackage.description ?: "")))
            }
            tables.add(Pair(elem.key, table))
        }

        return tables
    }

    private fun getHeaderCell(text: String): PdfPCell {
        val header = PdfPCell(Phrase(text))
        header.setBackgroundColor(Color.LIGHT_GRAY)
        header.setHorizontalAlignment(Element.ALIGN_CENTER)
        return header
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
