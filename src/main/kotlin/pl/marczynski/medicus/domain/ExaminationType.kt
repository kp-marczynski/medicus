package pl.marczynski.medicus.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.SequenceGenerator
import javax.persistence.Table
import javax.validation.constraints.NotNull

import java.io.Serializable

/**
 * A ExaminationType.
 */
@Entity
@Table(name = "examination_type")
class ExaminationType(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,

    @get: NotNull
    @Column(name = "name", nullable = false)
    var name: String? = null,

    @Column(name = "unit")
    var unit: String? = null,

    @Column(name = "min_good_value")
    var minGoodValue: Double? = null,

    @Column(name = "max_good_value")
    var maxGoodValue: Double? = null,

    @Column(name = "min_bad_value")
    var minBadValue: Double? = null,

    @Column(name = "max_bad_value")
    var maxBadValue: Double? = null,

    @ManyToOne
    @JsonIgnoreProperties("examinationTypes")
    var user: User? = null,

    @OneToMany(mappedBy = "examinationType")
    var examinations: MutableSet<Examination> = mutableSetOf()

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ExaminationType) return false
        if (other.id == null || id == null) return false

        return id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "ExaminationType{" +
        "id=$id" +
        ", name='$name'" +
        ", unit='$unit'" +
        ", minGoodValue=$minGoodValue" +
        ", maxGoodValue=$maxGoodValue" +
        ", minBadValue=$minBadValue" +
        ", maxBadValue=$maxBadValue" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
