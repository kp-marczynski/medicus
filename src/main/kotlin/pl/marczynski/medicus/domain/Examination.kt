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

/**
 * A Examination.
 */
@Entity
@Table(name = "examination")
class Examination(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,

    @get: NotNull
    @Column(name = "value", nullable = false)
    var value: Double? = null,

    @Column(name = "value_modificator")
    var valueModificator: String? = null,

    @Column(name = "ignore_number_value")
    var ignoreNumberValue: Boolean? = null,

    @ManyToOne
    @JsonIgnoreProperties("examinations")
    var examinationType: ExaminationType? = null,

    @ManyToOne
    @JsonIgnoreProperties("examinations")
    var user: User? = null,

    @ManyToOne
    @JsonIgnoreProperties("examinations")
    var examinationPackage: ExaminationPackage? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Examination) return false
        if (other.id == null || id == null) return false

        return id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Examination{" +
        "id=$id" +
        ", value=$value" +
        ", valueModificator='$valueModificator'" +
        ", ignoreNumberValue='$ignoreNumberValue'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
