package pl.marczynski.medicus.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.Type

import javax.validation.constraints.NotNull

import java.io.Serializable
import java.time.LocalDate
import javax.persistence.*

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

    @ManyToOne
    @JsonIgnoreProperties("procedures")
    var user: User? = null,

    @ManyToMany
    @JoinTable(name = "procedure_visited_doctor",
        joinColumns = [JoinColumn(name = "procedure_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "visited_doctor_id", referencedColumnName = "id")])
    var visitedDoctors: MutableSet<VisitedDoctor>? = mutableSetOf(),

    @Column(name = "appointment_id")
    var appointment: Long? = null

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
