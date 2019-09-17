package pl.marczynski.medicus.service

import pl.marczynski.medicus.repository.UserRepository

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import com.itextpdf.text.BaseColor
import com.itextpdf.text.Chunk
import com.itextpdf.text.Document
import com.itextpdf.text.DocumentException
import com.itextpdf.text.Element
import com.itextpdf.text.FontFactory
import com.itextpdf.text.Paragraph
import com.itextpdf.text.Phrase
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import pl.marczynski.medicus.domain.MedicalHistoryReport
import pl.marczynski.medicus.repository.ExaminationPackageRepository
import pl.marczynski.medicus.security.getCurrentUserLogin
import java.io.ByteArrayOutputStream
import java.sql.Timestamp

/**
 * Service class for managing users.
 */
@Service
@Transactional
class MedicalHistoryReportService(
    private val userRepository: UserRepository,
    private val examinationPackageRepository: ExaminationPackageRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun getReport(): MedicalHistoryReport {
        var document = Document()
        var out = ByteArrayOutputStream()

        try {
            PdfWriter.getInstance(document, out)
            document.open()

            // Add Text to PDF file ->
            val font = FontFactory.getFont(FontFactory.HELVETICA, 14f, BaseColor.BLACK)
            var para = Paragraph("Medical History Report - " + getCurrentUserLogin().orElse("") + " - " + Timestamp(System.currentTimeMillis()), font)
            para.setAlignment(Element.ALIGN_CENTER)
            document.add(para)
            document.add(Chunk.NEWLINE)

            var examinationPackagesTable = PdfPTable(3)
            // Add PDF Table Header ->
            for (headerTile in arrayOf("Date", "Title", "Examinations")) {
                var header = PdfPCell()
                val headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD)
                header.setBackgroundColor(BaseColor.LIGHT_GRAY)
                header.setHorizontalAlignment(Element.ALIGN_CENTER)
                header.setBorderWidth(2f)
                header.setPhrase(Phrase(headerTile, headFont))
                examinationPackagesTable.addCell(header)
            }
            val examinationPackages = examinationPackageRepository.findAll()
            for (examinationPackage in examinationPackages) {
                val idCell = PdfPCell(Phrase(examinationPackage.date.toString()))
                idCell.setPaddingLeft(4f)
                idCell.setVerticalAlignment(Element.ALIGN_MIDDLE)
                idCell.setHorizontalAlignment(Element.ALIGN_CENTER)
                examinationPackagesTable.addCell(idCell)

                val firstNameCell = PdfPCell(Phrase(examinationPackage.title))
                firstNameCell.setPaddingLeft(4f)
                firstNameCell.setVerticalAlignment(Element.ALIGN_MIDDLE)
                firstNameCell.setHorizontalAlignment(Element.ALIGN_LEFT)
                examinationPackagesTable.addCell(firstNameCell)

                var examinationTable = PdfPTable(2)
                examinationPackage.examinations?.forEach {
                    examinationTable.addCell(PdfPCell(Phrase(it.examinationType?.name)))
                    examinationTable.addCell(PdfPCell(Phrase(it.value.toString() + " " + it.examinationType?.unit)))
                }
                val lastNameCell = PdfPCell(examinationTable)
                examinationPackagesTable.addCell(lastNameCell)
            }
            document.add(examinationPackagesTable)

            document.close()
        } catch (e: DocumentException) {
            LoggerFactory.getLogger(this::class.java).error(e.toString())
        }

        return MedicalHistoryReport(getCurrentUserLogin().orElse(null), out.toByteArray())
    }
}
