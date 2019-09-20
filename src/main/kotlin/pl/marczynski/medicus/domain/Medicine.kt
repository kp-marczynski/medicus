package pl.marczynski.medicus.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.Type

import javax.validation.constraints.NotNull

import java.io.Serializable
import javax.persistence.*

/**
 * A Medicine.
 */
@Entity
@Table(name = "medicine")
class Medicine(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,

    @get: NotNull
    @Column(name = "name", nullable = false)
    var name: String? = null,

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "indication")
    var indication: String? = null,

    @OneToOne(optional = false, cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    var leaflet: File? = null,

    @ManyToOne
    @JsonIgnoreProperties("medicines")
    var user: User? = null,

    @OneToMany(mappedBy = "medicine")
    var ownedMedicines: MutableSet<OwnedMedicine>? = mutableSetOf(),

    @ManyToMany(mappedBy = "medicines")
    @JsonIgnore
    var treatments: MutableSet<Treatment>? = mutableSetOf()

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Medicine) return false
        if (other.id == null || id == null) return false

        return id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Medicine{" +
        "id=$id" +
        ", name='$name'" +
        ", indication='$indication'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
