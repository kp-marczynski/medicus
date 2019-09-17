package pl.marczynski.medicus.service

import pl.marczynski.medicus.repository.AuthorityRepository
import pl.marczynski.medicus.repository.UserRepository

import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
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
import pl.marczynski.medicus.security.getCurrentUserLogin
import java.io.ByteArrayOutputStream

/**
 * Service class for managing users.
 */
@Service
@Transactional
class MedicalHistoryReportService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authorityRepository: AuthorityRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun getReport(): MedicalHistoryReport {
        var document = Document()
        var out = ByteArrayOutputStream()

        try {
            PdfWriter.getInstance(document, out)
            document.open()

            // Add Text to PDF file ->
            val font = FontFactory.getFont(FontFactory.COURIER, 14f, BaseColor.BLACK)
            var para = Paragraph("Customer Table", font)
            para.setAlignment(Element.ALIGN_CENTER)
            document.add(para)
            document.add(Chunk.NEWLINE)

            var table = PdfPTable(3)
            // Add PDF Table Header ->
            for (headerTile in arrayOf("ID", "First Name", "Last Name")) {
                var header = PdfPCell()
                val headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD)
                header.setBackgroundColor(BaseColor.LIGHT_GRAY)
                header.setHorizontalAlignment(Element.ALIGN_CENTER)
                header.setBorderWidth(2f)
                header.setPhrase(Phrase(headerTile, headFont))
                table.addCell(header)
            }

            document.add(table)

            document.close()
        } catch (e: DocumentException) {
            LoggerFactory.getLogger(this::class.java).error(e.toString())
        }

        return MedicalHistoryReport(getCurrentUserLogin().orElse(null), out.toByteArray())
    }
}
