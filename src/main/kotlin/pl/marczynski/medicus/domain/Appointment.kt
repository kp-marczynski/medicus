package pl.marczynski.medicus.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.Type

import javax.validation.constraints.NotNull

import java.io.Serializable
import java.time.LocalDate
import javax.persistence.*

/**
 * A Appointment.
 */
@Entity
@Table(name = "appointment")
class Appointment(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,

    @get: NotNull
    @Column(name = "date", nullable = false)
    var date: LocalDate? = null,

    @get: NotNull
    @Column(name = "title", nullable = false)
    var title: String? = null,

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "description")
    var description: String? = null,

    @OneToOne(optional = true, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(unique = true)
    var descriptionScan: File? = null,

//    @OneToMany(mappedBy = "appointment", fetch = FetchType.EAGER)
    @OneToMany
    @JoinColumn(name = "appointment_id", nullable = true)
    var examinationPackages: MutableSet<ExaminationPackage>? = mutableSetOf(),

//    @OneToMany(mappedBy = "appointment", fetch = FetchType.EAGER)
    @OneToMany
    @JoinColumn(name = "appointment_id", nullable = true)
    var procedures: MutableSet<Procedure>? = mutableSetOf(),

    @ManyToOne
    @JsonIgnoreProperties("appointments")
    var user: User? = null,

    @ManyToMany
    @JoinTable(name = "appointment_treatment",
        joinColumns = [JoinColumn(name = "appointment_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "treatment_id", referencedColumnName = "id")])
    var treatments: MutableSet<Treatment>? = mutableSetOf(),

    @ManyToMany
    @JoinTable(name = "appointment_symptom",
        joinColumns = [JoinColumn(name = "appointment_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "symptom_id", referencedColumnName = "id")])
    var symptoms: MutableSet<Symptom>? = mutableSetOf(),

    @ManyToMany
    @JoinTable(name = "appointment_visited_doctor",
        joinColumns = [JoinColumn(name = "appointment_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "visited_doctor_id", referencedColumnName = "id")])
    var visitedDoctors: MutableSet<VisitedDoctor>? = mutableSetOf()

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Appointment) return false
        if (other.id == null || id == null) return false

        return id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Appointment{" +
        "id=$id" +
        ", date='$date'" +
        ", title='$title'" +
        ", description='$description'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
