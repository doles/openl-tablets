package org.openl.rules.ui.search;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openl.rules.search.GroupOperator;
import org.openl.rules.search.ISearchConstants;
import org.openl.rules.search.OpenLAdvancedSearch;
import org.openl.rules.search.OpenLSavedSearch;
import org.openl.rules.search.SearchConditionElement;
import org.openl.rules.ui.ProjectModel;
import org.openl.rules.ui.WebStudio;
import org.openl.rules.web.jsf.util.FacesUtils;
import static org.openl.rules.web.jsf.util.FacesUtils.createSelectItems;
import org.openl.rules.webstudio.web.util.WebStudioUtils;
import org.openl.util.AStringBoolOperator;

/**
 * JSF managed bean, session scope.
 */
public class OpenLAdvancedSearchBean {
    
    public class ColumnSearchElementBean extends SearchElementBean {
        public ColumnSearchElementBean(int index) {
            super(index);
        }

        @Override
        protected SearchConditionElement getSearchConditionElement() {
            return search.getColumnElements()[index];
        }
    }

    public class SearchElementBean {
        protected int index;

        SearchElementBean(int index) {
            this.index = index;
        }

        public String getGroupOperatorName() {
            return getSearchConditionElement().getGroupOperator().getName();
        }

        public String getNotFlag() {
            return OpenLAdvancedSearch.nfValues[getSearchConditionElement().isNotFlag() ? 1 : 0];
        }

        public GroupOperator getOperator() {
            return getSearchConditionElement().getGroupOperator();
        }

        public String getOpType1() {
            return getSearchConditionElement().getOpType1();
        }

        public String getOpType2() {
            return getSearchConditionElement().getOpType2();
        }

        protected SearchConditionElement getSearchConditionElement() {
            return search.getTableElements()[index];
        }

        public String getType() {
            return getSearchConditionElement().getType();
        }

        public String getElementValueName() {
            return getSearchConditionElement().getElementValueName();
        }

        public String getElementValue() {
            return getSearchConditionElement().getElementValue();
        }

        public boolean isRequiredElementValueName() {
            return search.showElementValueName(getType());
        }

        public void setGroupOperatorName(String groupOperatorName) {
            getSearchConditionElement().setGroupOperator(GroupOperator.find(groupOperatorName));
        }

        public void setNotFlag(String flag) {
            getSearchConditionElement().setNotFlag(OpenLAdvancedSearch.nfValues[1].equals(flag));
        }

        public void setOpType1(String type) {
            getSearchConditionElement().setOpType1(type);
        }

        public void setOpType2(String type) {
            getSearchConditionElement().setOpType2(type);
        }

        public void setType(String type) {
            getSearchConditionElement().setType(type);
        }

        public void setElementValueName(String elementValueName) {
            getSearchConditionElement().setElementValueName(elementValueName);
        }

        public void setElementValue(String elementValue) {
            getSearchConditionElement().setElementValue(elementValue);
        }
    }
    /**
     * Request scope bean, holding flag if search run is required.
     */
    public static class SearchRequest {
        private boolean needSearch;
        private OpenLAdvancedSearchBean advancedSearchBean;
        private List<TableSearch> tableSearchList;

        public OpenLAdvancedSearchBean getAdvancedSearchBean() {
            return advancedSearchBean;
        }

        public String getSearchResult() {
            if (!isSearching() || !advancedSearchBean.isReady()) {
                return "";
            }

            ProjectModel model = WebStudioUtils.getWebStudio().getModel();
            return model.displayResult(model.runSearch(advancedSearchBean.search));
        }

        public List<TableSearch> getSearchResults() {
            if (!isSearching() || !advancedSearchBean.isReady()) {
                return Collections.emptyList();
            }
            if (tableSearchList == null) {
                ProjectModel model = WebStudioUtils.getWebStudio().getModel();                
                tableSearchList = model.getSearchList(model.runSearch(advancedSearchBean.search));
            }
            return tableSearchList;
        }

        public boolean isSearching() {
            return needSearch;
        }

        public String search() {
            needSearch = true;
            return null;
        }

        public void setAdvancedSearchBean(OpenLAdvancedSearchBean advancedSearchBean) {
            this.advancedSearchBean = advancedSearchBean;
        }
    }
    private final static Log log = LogFactory.getLog(OpenLAdvancedSearchBean.class);
    private static final SelectItem[] tableTypes;
    private static final SelectItem[] columnTypeValues;
    private static final SelectItem[] groupOperationValues;
    private static final SelectItem[] notFlagValues;

    private static final SelectItem[] typeValues;
    private static final SelectItem[] opTypeValues;
    private static final Map<String, Integer> tableType2Index;
    private String[] selectedTableTypes;
    private String newSearchName;

    private SearchElementBean[] tableElements;

    private SearchElementBean[] columnElements;

    private final OpenLAdvancedSearch search = new OpenLAdvancedSearch();

    static {
        tableTypes = createSelectItems(OpenLAdvancedSearch.existingTableTypes);
        columnTypeValues = createSelectItems(ISearchConstants.colTypeValues);
        groupOperationValues = createSelectItems(GroupOperator.names);
        notFlagValues = createSelectItems(OpenLAdvancedSearch.nfValues);
        typeValues = createSelectItems(ISearchConstants.typeValues);
        opTypeValues = createSelectItems(AStringBoolOperator.allNames());

        tableType2Index = new HashMap<String, Integer>();
        for (int i = 0; i < OpenLAdvancedSearch.existingTableTypes.length; i++) {
            tableType2Index.put(OpenLAdvancedSearch.existingTableTypes[i], i);
        }
    }

    public OpenLAdvancedSearchBean() {
        updateTableElements();
        updateColumnElements();
    }

    public synchronized String addColCondition() {
        search.editAction(ISearchConstants.COL_ADD_ACTION + FacesUtils.getRequestParameter("index"));
        updateColumnElements();
        return null;
    }

    public synchronized String addCondition() {
        search.editAction(ISearchConstants.ADD_ACTION + FacesUtils.getRequestParameter("index"));
        updateTableElements();
        return null;
    }

    public String applySearch() {
        WebStudio webStudio = WebStudioUtils.getWebStudio();
        if (webStudio != null) {
            int index = -1;
            try {
                index = Integer.parseInt(FacesUtils.getRequestParameter("index"));
            } catch (NumberFormatException e) {
            }

            OpenLSavedSearch[] savedSearches = webStudio.getModel().getSavedSearches();
            if (savedSearches != null && index >= 0 && index < savedSearches.length) {
                applySearch(savedSearches[index]);
            }
        }

        return null;
    }

    private void applySearch(OpenLSavedSearch savedSearch) {
        search.setTableElements(savedSearch.getTableElements());
        search.setColumnElements(savedSearch.getColumnElements());
        updateColumnElements();
        updateTableElements();
        String types = savedSearch.getTableTypes();
        setSelectedTableTypes(types.trim().split(", *"));
    }

    public synchronized String deleteColCondition() {
        search.editAction(ISearchConstants.COL_DELETE_ACTION + FacesUtils.getRequestParameter("index"));
        updateColumnElements();
        return null;
    }

    public synchronized String deleteCondition() {
        search.editAction(ISearchConstants.DELETE_ACTION + FacesUtils.getRequestParameter("index"));
        updateTableElements();
        return null;
    }

    public synchronized SearchElementBean[] getColumnElements() {
        return columnElements;
    }

    public SelectItem[] getColumnTypeValues() {
        return columnTypeValues;
    }

    public SelectItem[] getGroupOperationValues() {
        return groupOperationValues;
    }

    public String getNewSearchName() {
        return newSearchName;
    }

    public SelectItem[] getNotFlagValues() {
        return notFlagValues;
    }

    public SelectItem[] getOpTypeValues() {
        return opTypeValues;
    }

    public OpenLSavedSearch[] getSavedSearches() {
        WebStudio webStudio = WebStudioUtils.getWebStudio();
        if (webStudio != null) {
            return webStudio.getModel().getSavedSearches();
        }
        return null;
    }

    public String[] getSelectedTableTypes() {
        return selectedTableTypes;
    }

    public String getStudioView() {
        WebStudio studio = WebStudioUtils.getWebStudio();
        return studio == null ? null : studio.getModel().getTableView(FacesUtils.getRequestParameter("view"));
    }

    public synchronized SearchElementBean[] getTableElements() {
        return tableElements;
    }

    public SelectItem[] getTableTypes() {
        return tableTypes;
    }

    public SelectItem[] getTypeValues() {
        return typeValues;
    }

    public boolean isReady() {
        return WebStudioUtils.isStudioReady();
    }

    public boolean isShowSearches() {
        OpenLSavedSearch[] savedSearches = getSavedSearches();
        return savedSearches != null && savedSearches.length > 0;
    }

    public boolean isStudioReadOnly() {
        WebStudio webStudio = WebStudioUtils.getWebStudio();
        return webStudio == null || webStudio.getModel().isReadOnly();
    }

    public String save() {
        WebStudio webStudio = WebStudioUtils.getWebStudio();
        if (webStudio != null) {
            try {
                OpenLSavedSearch savedSearch = new OpenLSavedSearch(search.getColumnElements(), search
                        .getTableElements(), selectedTableTypes);
                savedSearch.setName(getNewSearchName());
                webStudio.getModel().saveSearch(savedSearch);
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Failed to save search", e.getMessage()));
                log.error("failed to save search", e);
            }
        }
        return null;
    }

    public void setNewSearchName(String newSearchName) {
        this.newSearchName = newSearchName;
    }

    public void setSelectedTableTypes(String[] selectedTableTypes) {
        this.selectedTableTypes = selectedTableTypes;

        boolean[] selected = new boolean[tableTypes.length];
        for (String selectedTableType : selectedTableTypes) {
            Integer index = tableType2Index.get(selectedTableType);
            if (index != null) {
                selected[index] = true;
            }
        }

        for (int i = 0; i < selected.length; i++) {
            search.selectTableType(i, selected[i]);
        }
    }

    private void updateColumnElements() {
        columnElements = new SearchElementBean[search.getColumnElements().length];
        for (int i = 0; i < columnElements.length; ++i) {
            columnElements[i] = new ColumnSearchElementBean(i);
        }
    }

    private void updateTableElements() {
        tableElements = new SearchElementBean[search.getTableElements().length];
        for (int i = 0; i < tableElements.length; ++i) {
            tableElements[i] = new SearchElementBean(i);
        }
    }
}
