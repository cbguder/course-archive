<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<%-- This file generated by Sakai App Builder --%>
<f:loadBundle basename="messages" var="msgs"/>

<f:view>
	<sakai:view_container title="Course Archive Tool">
		<style type="text/css">
			@import url("/coursearchive-tool/css/CourseArchive.css");
		</style>

		<h:form id="listItems">
			<sakai:tool_bar>
				<sakai:tool_bar_item action="#{CourseArchiveBean.processActionList}" value="#{msgs.list_items}" />
			</sakai:tool_bar>
		</h:form>

		<sakai:view_content>
			<h:form id="addUpdateItem">
				<sakai:view_title value="#{msgs.item_edit_page_title}"/>

				<sakai:messages />

				<sakai:panel_edit>
					<h:outputText value="#{msgs.item_code}"/>
					<h:inputText value="#{CourseArchiveBean.itemCode}"/>

					<h:outputText value="#{msgs.item_term}"/>
					<h:inputText value="#{CourseArchiveBean.itemTerm}"/>

					<h:outputText value="#{msgs.item_name}"/>
					<h:inputText value="#{CourseArchiveBean.itemName}"/>

					<h:outputText value="#{msgs.item_primary_instructor}"/>
					<h:inputText value="#{CourseArchiveBean.itemPrimaryInstructor}"/>

					<h:outputText value="#{msgs.item_other_instructors}"/>
					<h:inputTextarea value="#{CourseArchiveBean.itemOtherInstructors}"/>

					<h:outputText value="#{msgs.item_assistants}"/>
					<h:inputTextarea value="#{CourseArchiveBean.itemAssistants}" rows="5"/>

					<h:outputText value="#{msgs.item_comments}"/>
					<h:inputTextarea value="#{CourseArchiveBean.itemComments}" rows="5" cols="40"/>

					<h:outputText value="#{msgs.item_public}"/>
					<h:selectBooleanCheckbox value="#{CourseArchiveBean.itemPublic}"/>

					<h:outputText value="#{msgs.item_delegate}"/>
					<h:inputText value="#{CourseArchiveBean.itemDelegateEid}"/>
				</sakai:panel_edit>

				<sakai:view_title value="Letter Grades"/>

				<table id="gradesTable" class="listHier">
					<thead>
						<tr>
							<th class="headerAlignment">A</th>
							<th class="headerAlignment">A-</th>
							<th class="headerAlignment">B+</th>
							<th class="headerAlignment">B</th>
							<th class="headerAlignment">B-</th>
							<th class="headerAlignment">C+</th>
							<th class="headerAlignment">C</th>
							<th class="headerAlignment">C-</th>
							<th class="headerAlignment">D+</th>
							<th class="headerAlignment">D</th>
							<th class="headerAlignment">F</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td><h:inputText value="#{CourseArchiveBean.itemA}"/></td>
							<td><h:inputText value="#{CourseArchiveBean.itemA_MINUS}"/></td>
							<td><h:inputText value="#{CourseArchiveBean.itemB_PLUS}"/></td>
							<td><h:inputText value="#{CourseArchiveBean.itemB}"/></td>
							<td><h:inputText value="#{CourseArchiveBean.itemB_MINUS}"/></td>
							<td><h:inputText value="#{CourseArchiveBean.itemC_PLUS}"/></td>
							<td><h:inputText value="#{CourseArchiveBean.itemC}"/></td>
							<td><h:inputText value="#{CourseArchiveBean.itemC_MINUS}"/></td>
							<td><h:inputText value="#{CourseArchiveBean.itemD_PLUS}"/></td>
							<td><h:inputText value="#{CourseArchiveBean.itemD}"/></td>
							<td><h:inputText value="#{CourseArchiveBean.itemF}"/></td>
						</tr>
					</tbody>
				</table>

				<sakai:view_title value="Assignment and Exam Grades"/>

				<h:dataTable
					value="#{CourseArchiveBean.itemAssignments}"
					var="entry"
					headerClass="headerAlignment"
					columnClasses="deleteCol,titleCol,gradeCol,gradeCol,gradeCol,gradeCol,gradeCol,dateCol"
					styleClass="listHier assignmentsTable">
					<h:column>
						<f:facet name="header">
							<h:outputText value="#{msgs.assignment_delete}"/>
						</f:facet>
						<h:selectBooleanCheckbox id="itemSelect" value="#{entry.selected}"/>
					</h:column>

					<h:column>
						<f:facet name="header">
							<h:outputText value="#{msgs.assignment_title}"/>
						</f:facet>
						<h:inputText value="#{entry.item.title}"/>
					</h:column>

					<h:column>
						<f:facet name="header">
							<h:outputText value="#{msgs.assignment_weight}"/>
						</f:facet>
						<h:inputText value="#{entry.item.weight}"/>
					</h:column>

					<h:column>
						<f:facet name="header">
							<h:outputText value="#{msgs.assignment_max_grade}"/>
						</f:facet>
						<h:inputText value="#{entry.item.maxGrade}"/>
					</h:column>

					<h:column>
						<f:facet name="header">
							<h:outputText value="#{msgs.assignment_mean_grade}"/>
						</f:facet>
						<h:inputText value="#{entry.item.meanGrade}"/>
					</h:column>

					<h:column>
						<f:facet name="header">
							<h:outputText value="#{msgs.assignment_median_grade}"/>
						</f:facet>
						<h:inputText value="#{entry.item.medianGrade}"/>
					</h:column>

					<h:column>
						<f:facet name="header">
							<h:outputText value="#{msgs.assignment_standard_deviation}"/>
						</f:facet>
						<h:inputText value="#{entry.item.standardDeviation}"/>
					</h:column>

					<h:column>
						<f:facet name="header">
							<h:outputText value="#{msgs.assignment_date}"/>
						</f:facet>
						<h:inputText value="#{entry.item.date}">
							<f:convertDateTime type="date" pattern="dd/MM/yyyy" />
						</h:inputText>
					</h:column>
				</h:dataTable>

				<h:commandButton value="Add New Grade" action="#{CourseArchiveBean.processActionAddAssignment}"/>

				<sakai:button_bar>
					<h:commandButton styleClass="active" accesskey="s" value="#{msgs.save}" action="#{CourseArchiveBean.processActionUpdate}"/>
					<h:commandButton accesskey="x" value="#{msgs.cancel}" action="#{CourseArchiveBean.processActionShow}"/>
				</sakai:button_bar>
			</h:form>
 		</sakai:view_content>
	</sakai:view_container>
</f:view>
