/**

 * <a href="http://www.openolat.org">
 * OpenOLAT - Online Learning and Training</a><br>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); <br>
 * you may not use this file except in compliance with the License.<br>
 * You may obtain a copy of the License at the
 * <a href="http://www.apache.org/licenses/LICENSE-2.0">Apache homepage</a>
 * <p>
 * Unless required by applicable law or agreed to in writing,<br>
 * software distributed under the License is distributed on an "AS IS" BASIS, <br>
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br>
 * See the License for the specific language governing permissions and <br>
 * limitations under the License.
 * <p>
 * Initial code contributed and copyrighted by<br>
 * frentix GmbH, http://www.frentix.com
 * <p>
 */
package org.olat.modules.curriculum.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.olat.basesecurity.Group;
import org.olat.basesecurity.model.GroupImpl;
import org.olat.core.id.Persistable;
import org.olat.core.util.StringHelper;
import org.olat.modules.curriculum.Curriculum;
import org.olat.modules.curriculum.CurriculumElement;
import org.olat.modules.curriculum.CurriculumElementManagedFlag;
import org.olat.modules.curriculum.CurriculumElementStatus;
import org.olat.modules.curriculum.CurriculumElementToTaxonomyLevel;
import org.olat.modules.curriculum.CurriculumElementType;

/**
 * 
 * Initial date: 9 févr. 2018<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
@Entity(name="curriculumelement")
@Table(name="o_cur_curriculum_element")
public class CurriculumElementImpl implements CurriculumElement, Persistable {

	private static final long serialVersionUID = 547658342562646552L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id", nullable=false, unique=true, insertable=true, updatable=false)
	private Long key;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="creationdate", nullable=false, insertable=true, updatable=false)
	private Date creationDate;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="lastmodified", nullable=false, insertable=true, updatable=true)
	private Date lastModified;
	
	/** Only used for order by */
	@GeneratedValue
	@Column(name="pos", insertable=false, updatable=false)//TODO order hack
	private Long pos;
	
	@Column(name="c_identifier", nullable=true, insertable=true, updatable=true)
	private String identifier;
	@Column(name="c_displayname", nullable=true, insertable=true, updatable=true)
	private String displayName;
	@Column(name="c_description", nullable=true, insertable=true, updatable=true)
	private String description;
	
	@Column(name="c_status", nullable=true, insertable=true, updatable=true)
	private String statusString;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="c_begin", nullable=true, insertable=true, updatable=true)
	private Date beginDate;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="c_end", nullable=true, insertable=true, updatable=true)
	private Date endDate;
	
	@Column(name="c_external_id", nullable=true, insertable=true, updatable=true)
	private String externalId;
	@Column(name="c_managed_flags", nullable=true, insertable=true, updatable=true)
	private String managedFlagsString;
	
	@Column(name="c_m_path_keys", nullable=true, insertable=true, updatable=true)
	private String materializedPathKeys;
	
	@ManyToOne(targetEntity=GroupImpl.class,fetch=FetchType.LAZY,optional=false)
	@JoinColumn(name="fk_group", nullable=false, insertable=true, updatable=false)
	private Group group;
	
	@ManyToOne(targetEntity=CurriculumElementImpl.class)
	@JoinColumn(name="fk_parent", nullable=true, insertable=true, updatable=true)
	private CurriculumElement parent;
	
	@OneToMany(targetEntity=CurriculumElementImpl.class, mappedBy="parent", fetch=FetchType.LAZY,
			orphanRemoval=true, cascade={CascadeType.REMOVE})
	@OrderColumn(name="pos")
	private List<CurriculumElement> children;
	
	@ManyToOne(targetEntity=CurriculumImpl.class)
	@JoinColumn(name="fk_curriculum", nullable=true, insertable=true, updatable=true)
	private Curriculum curriculum;
	
	@ManyToOne(targetEntity=CurriculumElementTypeImpl.class,fetch=FetchType.LAZY,optional=true)
	@JoinColumn(name="fk_type", nullable=true, insertable=true, updatable=true)
	private CurriculumElementType type;
	
	@OneToMany(targetEntity=CurriculumElementToTaxonomyLevelImpl.class, fetch=FetchType.LAZY)
	@JoinColumn(name="fk_cur_element")
	private Set<CurriculumElementToTaxonomyLevel> taxonomyLevels;
	
	@Override
	public Long getKey() {
		return key;
	}
	
	public void setKey(Long key) {
		this.key = key;
	}

	@Override
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Override
	public Date getLastModified() {
		return lastModified;
	}

	@Override
	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}

	@Override
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public CurriculumElementStatus getStatus() {
		if(StringHelper.containsNonWhitespace(statusString)) {
			return CurriculumElementStatus.valueOf(statusString);
		}
		return null;
	}

	@Override
	public void setStatus(CurriculumElementStatus status) {
		if(status == null) {
			statusString = null;
		} else {
			statusString = status.name();
		}
	}

	public String getStatusString() {
		return statusString;
	}

	public void setStatusString(String statusString) {
		this.statusString = statusString;
	}

	@Override
	public Date getBeginDate() {
		return beginDate;
	}

	@Override
	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	@Override
	public Date getEndDate() {
		return endDate;
	}

	@Override
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Override
	public String getExternalId() {
		return externalId;
	}

	@Override
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	@Override
	public String getMaterializedPathKeys() {
		return materializedPathKeys;
	}

	public void setMaterializedPathKeys(String materializedPathKeys) {
		this.materializedPathKeys = materializedPathKeys;
	}

	public String getManagedFlagsString() {
		return managedFlagsString;
	}

	public void setManagedFlagsString(String managedFlagsString) {
		this.managedFlagsString = managedFlagsString;
	}

	@Override
	public CurriculumElementManagedFlag[] getManagedFlags() {
		return CurriculumElementManagedFlag.toEnum(managedFlagsString);
	}

	@Override
	public void setManagedFlags(CurriculumElementManagedFlag[] flags) {
		managedFlagsString = CurriculumElementManagedFlag.toString(flags);
	}

	@Override
	public Long getPos() {
		return pos;
	}
	
	public void setPos(Long pos) {
		this.pos = pos;
	}

	@Override
	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	@Override
	public CurriculumElement getParent() {
		return parent;
	}

	public void setParent(CurriculumElement parent) {
		this.parent = parent;
	}

	public List<CurriculumElement> getChildren() {
		if(children == null) {
			children = new ArrayList<>();
		}
		return children;
	}

	public void setChildren(List<CurriculumElement> children) {
		this.children = children;
	}

	@Override
	public Curriculum getCurriculum() {
		return curriculum;
	}

	public void setCurriculum(Curriculum curriculum) {
		this.curriculum = curriculum;
	}

	@Override
	public Set<CurriculumElementToTaxonomyLevel> getTaxonomyLevels() {
		if(taxonomyLevels == null) {
			taxonomyLevels = new HashSet<>();
		}
		return taxonomyLevels;
	}

	public void setTaxonomyLevels(Set<CurriculumElementToTaxonomyLevel> taxonomyLevels) {
		this.taxonomyLevels = taxonomyLevels;
	}

	@Override
	public CurriculumElementType getType() {
		return type;
	}
	
	public void setType(CurriculumElementType type) {
		this.type = type;
	}

	@Override
	public int hashCode() {
		return key == null ? 28562153 : key.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		}
		if(obj instanceof CurriculumElementImpl) {
			CurriculumElementImpl el = (CurriculumElementImpl)obj;
			return getKey() != null && getKey().equals(el.getKey());
		}
		return false;
	}

	@Override
	public boolean equalsByPersistableKey(Persistable persistable) {
		return equals(persistable);
	}
}
