package pl.marczynski.medicus.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.Type

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Lob
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.SequenceGenerator
import javax.persistence.Table
import javax.validation.constraints.NotNull

import java.io.Serializable
import java.time.LocalDate

/**
 * A Treatment.
 */
@Entity
@Table(name = "treatment")
class Treatment(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,

    @get: NotNull
    @Column(name = "start_date", nullable = false)
    var startDate: LocalDate? = null,

    @Column(name = "end_date")
    var endDate: LocalDate? = null,

    @Lob
        @Type(type = "org.hibernate.type.TextType")
    @Column(name = "description", nullable = false)
    var description: String? = null,

    @Lob
    @Column(name = "description_scan")
    var descriptionScan: ByteArray? = null,

    @Column(name = "description_scan_content_type")
    var descriptionScanContentType: String? = null,

    @OneToMany(mappedBy = "treatment")
    var visitedDoctors: MutableSet<VisitedDoctor> = mutableSetOf(),

    @ManyToOne
    @JsonIgnoreProperties("treatments")
    var user: User? = null,

    @ManyToMany
    @JoinTable(name = "treatment_medicine",
        joinColumns = [JoinColumn(name = "treatment_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "medicine_id", referencedColumnName = "id")])
    var medicines: MutableSet<Medicine> = mutableSetOf(),

    @ManyToMany(mappedBy = "treatments")
    @JsonIgnore
    var appointments: MutableSet<Appointment> = mutableSetOf()

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Treatment) return false
        if (other.id == null || id == null) return false

        return id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Treatment{" +
        "id=$id" +
        ", startDate='$startDate'" +
        ", endDate='$endDate'" +
        ", description='$description'" +
        ", descriptionScan='$descriptionScan'" +
        ", descriptionScanContentType='$descriptionScanContentType'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
