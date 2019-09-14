package pl.marczynski.medicus.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.Type

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Lob
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.SequenceGenerator
import javax.persistence.Table
import javax.validation.constraints.NotNull

import java.io.Serializable
import java.time.LocalDate

/**
 * A Procedure.
 */
@Entity
@Table(name = "procedure")
class Procedure(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,

    @get: NotNull
    @Column(name = "date", nullable = false)
    var date: LocalDate? = null,

    @Lob
        @Type(type = "org.hibernate.type.TextType")
    @Column(name = "description", nullable = false)
    var description: String? = null,

    @Lob
    @Column(name = "description_scan")
    var descriptionScan: ByteArray? = null,

    @Column(name = "description_scan_content_type")
    var descriptionScanContentType: String? = null,

    @OneToMany(mappedBy = "procedure")
    var visitedDoctors: MutableSet<VisitedDoctor> = mutableSetOf(),

    @ManyToOne
    @JsonIgnoreProperties("procedures")
    var user: User? = null,

    @ManyToOne
    @JsonIgnoreProperties("procedures")
    var appointment: Appointment? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Procedure) return false
        if (other.id == null || id == null) return false

        return id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Procedure{" +
        "id=$id" +
        ", date='$date'" +
        ", description='$description'" +
        ", descriptionScan='$descriptionScan'" +
        ", descriptionScanContentType='$descriptionScanContentType'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
