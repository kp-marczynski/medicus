package pl.marczynski.medicus.domain

import java.io.Serializable

/**
 * A medical history report.
 */
class MedicalHistoryReport(

    var title: String? = null,
    var report: ByteArray? = null,
    var reportContentType: String? = "application/pdf"
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MedicalHistoryReport) return false
        if (other.title == null || title == null) return false

        return title == other.title
    }

    override fun hashCode() = 31

    override fun toString() = "Examination{" +
        "title=$title" +
        ", reportType=$reportContentType" +
        ", report='$report'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
