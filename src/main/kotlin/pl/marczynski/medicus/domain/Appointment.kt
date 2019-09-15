package pl.marczynski.medicus.domain

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
    @Column(name = "appointment_type", nullable = false)
    var appointmentType: String? = null,

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "description", nullable = false)
    var description: String? = null,

    @Lob
    @Column(name = "description_scan")
    var descriptionScan: ByteArray? = null,

    @Column(name = "description_scan_content_type")
    var descriptionScanContentType: String? = null,

    @OneToMany(mappedBy = "appointment")
    var examinationPackages: MutableSet<ExaminationPackage>? = mutableSetOf(),

    @OneToMany(mappedBy = "appointment")
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
        ", appointmentType='$appointmentType'" +
        ", description='$description'" +
        ", descriptionScan='$descriptionScan'" +
        ", descriptionScanContentType='$descriptionScanContentType'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
