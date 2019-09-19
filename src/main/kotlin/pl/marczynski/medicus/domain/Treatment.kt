package pl.marczynski.medicus.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.Type

import javax.validation.constraints.NotNull

import java.io.Serializable
import java.time.LocalDate
import javax.persistence.*

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

    @Column(name = "title")
    var title: String? = null,

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "description")
    var description: String? = null,

    @OneToOne(optional = true, cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    var descriptionScan: File? = null,

    @ManyToOne
    @JsonIgnoreProperties("treatments")
    var user: User? = null,

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "treatment_medicine",
        joinColumns = [JoinColumn(name = "treatment_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "medicine_id", referencedColumnName = "id")])
    var medicines: MutableSet<Medicine>? = mutableSetOf(),

    @ManyToMany
    @JoinTable(name = "treatment_visited_doctor",
        joinColumns = [JoinColumn(name = "treatment_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "visited_doctor_id", referencedColumnName = "id")])
    var visitedDoctors: MutableSet<VisitedDoctor>? = mutableSetOf(),

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
        ", title='$title'" +
        ", description='$description'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
