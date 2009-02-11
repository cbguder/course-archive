<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<%-- This file generated by Sakai App Builder --%>
<f:loadBundle basename="messages" var="msgs"/>

<f:view>
	<sakai:view_container title="CourseArchive Tool">
		<style type="text/css">
			@import url("/coursearchive/css/CourseArchive.css");
		</style>

		<h:form id="addItem">
			<sakai:tool_bar>
				<sakai:tool_bar_item value="#{msgs.project_add_item_link}" action="#{CourseArchiveBean.processActionNew}" />
			</sakai:tool_bar>
		</h:form>

		<sakai:view_content>
			<h:form id="items">
			 	<sakai:view_title value="#{msgs.items_list_page_title}"/>

				<sakai:messages />

			 	<h:dataTable
			 		id="itemlist"
			 		value="#{CourseArchiveBean.allItems}"
			 		var="entry"
			 		columnClasses="firstColumn,secondColumn,thirdColumn,fourthColumn"
			 		headerClass="headerAlignment"
			 		styleClass="listHier">

					<h:column>
						<f:facet name="header">
							<h:outputText value=""/>
						</f:facet>
						<h:selectBooleanCheckbox id="itemSelect" value="#{entry.selected}" rendered="#{entry.canDelete}"/>
						<h:outputText value="" rendered="#{not entry.canDelete}"/>
					</h:column>

					<h:column>
						<f:facet name="header">
							<h:outputText value="#{msgs.items_list_title}"/>
						</f:facet>
						<h:commandLink id="showlink" action="#{CourseArchiveBean.processActionShow}">
							<h:outputText value="#{entry.item.title}"/>
						</h:commandLink>
					</h:column>

					<h:column>
						<f:facet name="header">
							<h:outputText value="#{msgs.items_list_date}"/>
						</f:facet>
						<h:outputText value="#{entry.item.dateCreated}">
							<f:convertDateTime type="both" dateStyle="medium" timeStyle="short" />
						</h:outputText>
					</h:column>

				</h:dataTable>

				<sakai:button_bar>
					<sakai:button_bar_item id="deleteItem" action="#{CourseArchiveBean.processActionDelete}" value="#{msgs.project_items_delete}"/>  
                </sakai:button_bar>

			 </h:form>
  		</sakai:view_content>	
	</sakai:view_container>
</f:view> 
