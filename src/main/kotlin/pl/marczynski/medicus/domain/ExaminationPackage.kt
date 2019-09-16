package pl.marczynski.medicus.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

import javax.validation.constraints.NotNull

import java.io.Serializable
import java.time.LocalDate
import javax.persistence.*

/**
 * A ExaminationPackage.
 */
@Entity
@Table(name = "examination_package")
class ExaminationPackage(

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
    @Column(name = "examination_package_scan")
    var examinationPackageScan: ByteArray? = null,

    @Column(name = "examination_package_scan_content_type")
    var examinationPackageScanContentType: String? = null,

    @OneToMany(mappedBy = "examinationPackage")
    var examinations: MutableSet<Examination> = mutableSetOf(),

    @ManyToOne
    @JsonIgnoreProperties("examinationPackages")
    var user: User? = null,

    @ManyToMany
    @JoinTable(name = "examination_package_visited_doctor",
        joinColumns = [JoinColumn(name = "examination_package_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "visited_doctor_id", referencedColumnName = "id")])
    var visitedDoctors: MutableSet<VisitedDoctor>? = mutableSetOf(),

    @Column(name = "appointment_id")
    var appointment: Long? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ExaminationPackage) return false
        if (other.id == null || id == null) return false

        return id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "ExaminationPackage{" +
        "id=$id" +
        ", date='$date'" +
        ", title='$title'" +
        ", examinationPackageScan='$examinationPackageScan'" +
        ", examinationPackageScanContentType='$examinationPackageScanContentType'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
