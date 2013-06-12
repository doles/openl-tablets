package org.openl.rules.webstudio.web.tableeditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.openl.commons.web.jsf.FacesUtils;
import org.openl.rules.table.ILogicalTable;
import org.openl.rules.table.IOpenLTable;
import org.openl.rules.table.properties.ITableProperties;
import org.openl.rules.table.properties.def.TablePropertyDefinition;
import org.openl.rules.table.properties.def.TablePropertyDefinitionUtils;
import org.openl.rules.table.properties.inherit.InheritanceLevel;
import org.openl.rules.tableeditor.model.TableEditorModel;
import org.openl.rules.tableeditor.renderkit.TableProperty;
import org.openl.rules.tableeditor.util.Constants;
import org.openl.rules.ui.ProjectModel;
import org.openl.rules.ui.WebStudio;
import org.openl.rules.validation.properties.dimentional.DispatcherTablesBuilder;
import org.openl.rules.webstudio.web.util.WebStudioUtils;
import org.openl.util.StringTool;

@ManagedBean
@ViewScoped
public class TableDetailsBean {
    private boolean editable;
    private List<PropertyRow> propertyRows;
    private Map<String, List<TableProperty>> groups;
    private Set<String> propsToRemove = new HashSet<String>();

    private String newTableUri;
    private String propertyToAdd;
    private String uri;

    public TableDetailsBean() {
        WebStudio studio = WebStudioUtils.getWebStudio();

        uri = FacesUtils.getRequestParameter(Constants.REQUEST_PARAM_URI);

        if (studio.getModel().getTable(uri) == null) {
            uri = studio.getTableUri();
        }

        IOpenLTable table = getTable();

        if (table.isCanContainProperties()) {
            editable = WebStudioUtils.getProjectModel().isCanEditTable(uri) && !table.getTechnicalName().startsWith(
                    DispatcherTablesBuilder.DEFAULT_DISPATCHER_TABLE_NAME);
            initPropertyGroups(table, table.getProperties());
        }
    }

    public void initPropertyGroups(IOpenLTable table, ITableProperties props) {
        groups = new LinkedHashMap<String, List<TableProperty>>();
        TablePropertyDefinition[] propDefinitions = TablePropertyDefinitionUtils
                .getDefaultDefinitionsForTable(table.getType());

        for (TablePropertyDefinition propDefinition : propDefinitions) {
            Object value = props.getPropertyValue(propDefinition.getName());
            if (value != null) {
                InheritanceLevel inheritanceLevel = props.getPropertyLevelDefinedOn(propDefinition.getName());

                TableProperty prop = new TableProperty(propDefinition);
                prop.setValue(value);
                prop.setInheritanceLevel(inheritanceLevel);
                if (InheritanceLevel.MODULE.equals(inheritanceLevel)
                        || InheritanceLevel.CATEGORY.equals(inheritanceLevel)) {
                    prop.setInheritedTableUri(getProprtiesTableUri(inheritanceLevel, props));
                }

                storeProperty(prop);
            }
        }
    }

    private void storeProperty(TableProperty prop) {
        String group = prop.getGroup();
        List<TableProperty> groupList = groups.get(group);
        if (groupList == null) {
            groupList = new ArrayList<TableProperty>();
            groups.put(group, groupList);
        }
        if (!groupList.contains(prop)) {
            groupList.add(prop);
        }
    }

    private void removeProperty(TableProperty prop) {
        String group = prop.getGroup();
        List<TableProperty> groupList = groups.get(group);
        if (groupList != null) {
            groupList.remove(prop);
        }
        if (groupList.isEmpty()) {
            groups.remove(group);
        }
    }

    public List<PropertyRow> getPropertyRows() {
        propertyRows = new ArrayList<PropertyRow>();

        for (String group : groups.keySet()) {
            propertyRows.add(new PropertyRow(PropertyRowType.GROUP, group));
            for (TableProperty prop : groups.get(group)) {
                propertyRows.add(new PropertyRow(PropertyRowType.PROPERTY, prop));
            }
        }

        return propertyRows;
    }

    public boolean isEditable() {
        return editable;
    }

    private String getProprtiesTableUri(InheritanceLevel inheritanceLevel, ITableProperties props) {
        String uri = null;
        ILogicalTable propertiesTable = props.getInheritedPropertiesTable(inheritanceLevel);
        if (propertiesTable != null) {
            String tableUri = propertiesTable.getSource().getUri();
            uri = StringTool.encodeURL(tableUri);
        }
        return uri;
    }

    public IOpenLTable getTable() {
        return WebStudioUtils.getWebStudio().getModel().getTable(uri);
    }

    public String getNewTableUri() {
        return newTableUri;
    }

    public void setNewTableUri(String newTableUri) {
        this.newTableUri = newTableUri;
    }

    public List<SelectItem> getPropertiesToAdd() {
        IOpenLTable table = getTable();
        List<SelectItem> propertiesToAdd = new ArrayList<SelectItem>();
        TablePropertyDefinition[] propDefinitions = TablePropertyDefinitionUtils
                .getDefaultDefinitionsForTable(table.getType(), InheritanceLevel.TABLE, true);
        Collection<String> currentProps = new TreeSet<String>();
        for (PropertyRow row : propertyRows) {
            if (row.getType().equals(PropertyRowType.PROPERTY)) {
                currentProps.add(((TableProperty) row.getData()).getName());
            }
        }

        for (TablePropertyDefinition propDefinition : propDefinitions) {
            String propName = propDefinition.getName();
            if (!currentProps.contains(propName)
                    && !"version".equals(propName)
                    && propDefinition.getDeprecation() == null) {
                propertiesToAdd.add(new SelectItem(propName, propDefinition.getDisplayName()));
            }
        }
        return propertiesToAdd;
    }

    public String getPropertyToAdd() {
        return propertyToAdd;
    }

    public void setPropertyToAdd(String propertyToAdd) {
        this.propertyToAdd = propertyToAdd;
    }

    public void addNew() {
        TablePropertyDefinition propDefinition = TablePropertyDefinitionUtils.getPropertyByName(propertyToAdd);
        storeProperty(new TableProperty(propDefinition));
        propsToRemove.remove(propertyToAdd);
    }

    public void remove(TableProperty prop) {
        removeProperty(prop);
        propsToRemove.add(prop.getName());
    }
    
    public boolean isChanged() throws Exception {
        if (propertyRows == null) {
            return false;
        }
        ITableProperties props = getTable().getProperties();
        for (PropertyRow row : propertyRows) {
            if (row.getType().equals(PropertyRowType.PROPERTY)) {
                TableProperty property = (TableProperty) row.getData();
                String name = property.getName();
                Object newValue = property.getValue();
                Object oldValue = props.getPropertyValue(name);
                boolean enumArray = property.isEnumArray();
                if ((enumArray && !Arrays.equals((Enum<?>[]) oldValue, (Enum<?>[]) newValue))
                        || (!enumArray && ObjectUtils.notEqual(oldValue, newValue))
                        || (!props.getAllProperties().containsKey(name))) {
                    return true;
                }
            }
        }
        for (String propToRemove : propsToRemove) {
            if (props.getAllProperties().containsKey(propToRemove))
                return true;
        }
        return false;
    }

    public void save() throws Exception {
        IOpenLTable table = getTable();

        WebStudio studio = WebStudioUtils.getWebStudio();
        ProjectModel model = studio.getModel();
        TableEditorModel tableEditorModel = model.getTableEditorModel(table);
        boolean toSave = false;

        ITableProperties props = table.getProperties();
        for (PropertyRow row : propertyRows) {
            if (row.getType().equals(PropertyRowType.PROPERTY)) {
                TableProperty property = (TableProperty) row.getData();
                String name = property.getName();
                Object newValue = property.getValue();
                Object oldValue = props.getPropertyValue(name);
                boolean enumArray = property.isEnumArray();

                if (newValue == null && oldValue != null) {
                    //if value is empty we have to delete it
                    propsToRemove.add(name);
                } else if ((enumArray && !Arrays.equals((Enum<?>[]) oldValue, (Enum<?>[]) newValue))
                        || (!enumArray && ObjectUtils.notEqual(oldValue, newValue))) {
                    tableEditorModel.setProperty(name,
                            newValue.getClass().isArray() && ArrayUtils.getLength(newValue) == 0 ? null : newValue);
                    toSave = true;
                }
            }
        }

        for (String propToRemove : propsToRemove) {
            tableEditorModel.removeProperty(propToRemove);
            toSave = true;
        }

        if (toSave) {
            if (studio.isUpdateSystemProperties()) {
                EditHelper.updateSystemProperties(table, tableEditorModel,
                    WebStudioUtils.getWebStudio().getSystemConfigManager().getStringProperty("user.mode"));
            }
            this.newTableUri = tableEditorModel.save();
            studio.rebuildModel();
        }
    }

    /*for (Constraint constraint : constraints.getAll()) {
        if (constraint instanceof LessThanConstraint || constraint instanceof MoreThanConstraint) {
            String validator = constraint instanceof LessThanConstraint ? "lessThan" : "moreThan";
            String compareToField = (String) constraint.getParams()[0];
            String compareToFieldId = "_" + id.replaceFirst(prop.getName() + "(?=$)", compareToField);
            TableProperty compareToProperty = getProperty(compareToField);
            String compareToPropertyDisplayName = compareToProperty == null ? "" : compareToProperty
                    .getDisplayName();
            result.append(renderJSBody("new Validation('_" + id + "', '" + validator
                    + "', 'blur', {compareToFieldId:'" + compareToFieldId + "',messageParams:'"
                    + compareToPropertyDisplayName + "'})"));
        }
    }*/

}
