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
import javax.persistence.SequenceGenerator
import javax.persistence.Table

import java.io.Serializable

/**
 * A VisitedDoctor.
 */
@Entity
@Table(name = "visited_doctor")
class VisitedDoctor(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,

    @Lob
        @Type(type = "org.hibernate.type.TextType")
    @Column(name = "opinion")
    var opinion: String? = null,

    @ManyToOne
    @JsonIgnoreProperties("visitedDoctors")
    var doctor: Doctor? = null,

    @ManyToOne
    @JsonIgnoreProperties("visitedDoctors")
    var appointment: Appointment? = null,

    @ManyToOne
    @JsonIgnoreProperties("visitedDoctors")
    var procedure: Procedure? = null,

    @ManyToOne
    @JsonIgnoreProperties("visitedDoctors")
    var treatment: Treatment? = null,

    @ManyToOne
    @JsonIgnoreProperties("visitedDoctors")
    var examinationPackage: ExaminationPackage? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is VisitedDoctor) return false
        if (other.id == null || id == null) return false

        return id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "VisitedDoctor{" +
        "id=$id" +
        ", opinion='$opinion'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
