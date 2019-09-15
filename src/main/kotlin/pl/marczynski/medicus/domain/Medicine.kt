package pl.marczynski.medicus.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.Type

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Lob
import javax.persistence.ManyToMany
import javax.persistence.SequenceGenerator
import javax.persistence.Table
import javax.validation.constraints.NotNull

import java.io.Serializable

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
    @Column(name = "indication", nullable = false)
    var indication: String? = null,

    @Lob
    @Column(name = "leaflet")
    var leaflet: ByteArray? = null,

    @Column(name = "leaflet_content_type")
    var leafletContentType: String? = null,

    @Column(name = "language")
    var language: String? = null,

    @ManyToMany(mappedBy = "medicines")
    @JsonIgnore
    var treatments: MutableSet<Treatment> = mutableSetOf()

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
        ", leaflet='$leaflet'" +
        ", leafletContentType='$leafletContentType'" +
        ", language='$language'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
