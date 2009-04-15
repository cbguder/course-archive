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
				<sakai:tool_bar_item action="#{CourseArchiveBean.processActionList}" value="#{msgs.list_items}"/>
				<sakai:tool_bar_item action="#{CourseArchiveBean.processActionShowRoster}" value="#{msgs.show_roster}"/>
			</sakai:tool_bar>
		</h:form>

		<sakai:view_content>
			<h:form>
				<sakai:view_title value="#{CourseArchiveBean.itemTitle}"/>

				<sakai:panel_edit>
					<h:outputText value="#{msgs.item_code}"/>
					<h:outputText value="#{CourseArchiveBean.itemCode}"/>

					<h:outputText value="#{msgs.item_term}"/>
					<h:outputText value="#{CourseArchiveBean.itemTerm}"/>

					<h:outputText value="#{msgs.item_name}"/>
					<h:outputText value="#{CourseArchiveBean.itemName}"/>

					<h:outputText value="#{msgs.item_primary_instructor}"/>
					<h:outputText value="#{CourseArchiveBean.itemPrimaryInstructor}"/>

					<h:outputText value="#{msgs.item_other_instructors}"/>
					<h:outputText value="#{CourseArchiveBean.itemOtherInstructors}" escape="false">
						<f:converter converterId="NewlineConverter"/>
					</h:outputText>

					<h:outputText value="#{msgs.item_assistants}"/>
					<h:outputText value="#{CourseArchiveBean.itemAssistants}" escape="false">
						<f:converter converterId="NewlineConverter"/>
					</h:outputText>

					<h:outputText value="#{msgs.item_enrollment}"/>
					<h:outputText value="#{CourseArchiveBean.itemEnrollment} "/>

					<h:outputText value="#{msgs.item_comments}"/>
					<h:outputText value="#{CourseArchiveBean.itemComments}"/>

					<h:outputText value="#{msgs.item_syllabus_url}"/>
					<h:outputLink value="#{CourseArchiveBean.itemSyllabusURL}">
						<h:outputText value="#{CourseArchiveBean.itemSyllabusURL}"/>
					</h:outputLink>

					<h:outputText value="#{msgs.item_delegate}"/>
					<h:outputText value="#{CourseArchiveBean.itemDelegateName}"/>

					<h:outputText value="#{msgs.item_public}"/>
					<h:outputText value="#{CourseArchiveBean.itemPublic}"/>
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
							<td><h:outputText value="#{CourseArchiveBean.itemA}"/></td>
							<td><h:outputText value="#{CourseArchiveBean.itemA_MINUS}"/></td>
							<td><h:outputText value="#{CourseArchiveBean.itemB_PLUS}"/></td>
							<td><h:outputText value="#{CourseArchiveBean.itemB}"/></td>
							<td><h:outputText value="#{CourseArchiveBean.itemB_MINUS}"/></td>
							<td><h:outputText value="#{CourseArchiveBean.itemC_PLUS}"/></td>
							<td><h:outputText value="#{CourseArchiveBean.itemC}"/></td>
							<td><h:outputText value="#{CourseArchiveBean.itemC_MINUS}"/></td>
							<td><h:outputText value="#{CourseArchiveBean.itemD_PLUS}"/></td>
							<td><h:outputText value="#{CourseArchiveBean.itemD}"/></td>
							<td><h:outputText value="#{CourseArchiveBean.itemF}"/></td>
						</tr>
					</tbody>
				</table>

				<sakai:view_title value="Assignment and Exam Grades"/>

				<h:dataTable
					value="#{CourseArchiveBean.itemAssignments}"
					var="entry"
					headerClass="headerAlignment"
					columnClasses=",gradeCol,gradeCol,gradeCol,gradeCol,gradeCol,dateCol"
					styleClass="listHier">
					<h:column>
						<f:facet name="header">
							<h:outputText value="#{msgs.assignment_title}"/>
						</f:facet>
						<h:outputText value="#{entry.item.title}"/>
					</h:column>

					<h:column>
						<f:facet name="header">
							<h:outputText value="#{msgs.assignment_weight}"/>
						</f:facet>
						<h:outputText value="#{entry.item.weight}%"/>
					</h:column>

					<h:column>
						<f:facet name="header">
							<h:outputText value="#{msgs.assignment_max_grade}"/>
						</f:facet>
						<h:outputText value="#{entry.item.maxGrade}"/>
					</h:column>

					<h:column>
						<f:facet name="header">
							<h:outputText value="#{msgs.assignment_mean_grade}"/>
						</f:facet>
						<h:outputText value="#{entry.item.meanGrade}"/>
					</h:column>

					<h:column>
						<f:facet name="header">
							<h:outputText value="#{msgs.assignment_median_grade}"/>
						</f:facet>
						<h:outputText value="#{entry.item.medianGrade}"/>
					</h:column>

					<h:column>
						<f:facet name="header">
							<h:outputText value="#{msgs.assignment_standard_deviation}"/>
						</f:facet>
						<h:outputText value="#{entry.item.standardDeviation}"/>
					</h:column>

					<h:column>
						<f:facet name="header">
							<h:outputText value="#{msgs.assignment_date}"/>
						</f:facet>
						<h:outputText value="#{entry.item.date}">
							<f:convertDateTime type="date" dateStyle="medium"/>
						</h:outputText>
					</h:column>
				</h:dataTable>

				<sakai:view_title value="Syllabi"/>

				<h:dataTable
					value="#{CourseArchiveBean.itemSyllabi}"
					var="entry"
					headerClass="headerAlignment"
					styleClass="listHier">
					<h:column>
						<f:facet name="header">
							<h:outputText value="#{msgs.syllabus_title}"/>
						</f:facet>
						<h:commandLink id="showlink" action="#{CourseArchiveBean.processActionShowSyllabus}">
							<h:outputText value="#{entry.title}"/>
						</h:commandLink>
					</h:column>
				</h:dataTable>

				<sakai:button_bar>
					<h:commandButton value="#{msgs.edit}" action="#{CourseArchiveBean.processActionEdit}" rendered="#{CourseArchiveBean.itemCanEdit}"/>
					<h:commandButton value="Archive Syllabi" action="#{CourseArchiveBean.processActionArchiveSyllabi}" rendered="#{CourseArchiveBean.itemCanEdit}"/>
				</sakai:button_bar>
			</h:form>
		</sakai:view_content>
	</sakai:view_container>
</f:view>
