package pl.marczynski.medicus.domain

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Lob
import javax.persistence.SequenceGenerator
import javax.persistence.Table

import java.io.Serializable

/**
 * A File.
 */
@Entity
@Table(name = "file")
class File(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,

    @Lob
    @Column(name = "content")
    var content: ByteArray? = null,

    @Column(name = "content_content_type")
    var contentContentType: String? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is File) return false
        if (other.id == null || id == null) return false

        return id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "File{" +
        "id=$id" +
        ", content='$content'" +
        ", contentContentType='$contentContentType'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
