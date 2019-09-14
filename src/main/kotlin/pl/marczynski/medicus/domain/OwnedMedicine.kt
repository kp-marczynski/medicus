package pl.marczynski.medicus.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.SequenceGenerator
import javax.persistence.Table
import javax.validation.constraints.NotNull

import java.io.Serializable
import java.time.LocalDate

/**
 * A OwnedMedicine.
 */
@Entity
@Table(name = "owned_medicine")
class OwnedMedicine(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,

    @get: NotNull
    @Column(name = "doses", nullable = false)
    var doses: Int? = null,

    @get: NotNull
    @Column(name = "expiration_date", nullable = false)
    var expirationDate: LocalDate? = null,

    @ManyToOne
    @JsonIgnoreProperties("ownedMedicines")
    var medicine: Medicine? = null,

    @ManyToOne
    @JsonIgnoreProperties("ownedMedicines")
    var user: User? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OwnedMedicine) return false
        if (other.id == null || id == null) return false

        return id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "OwnedMedicine{" +
        "id=$id" +
        ", doses=$doses" +
        ", expirationDate='$expirationDate'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
