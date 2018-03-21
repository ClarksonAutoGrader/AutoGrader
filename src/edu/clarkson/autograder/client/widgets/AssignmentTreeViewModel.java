/*
	Autograder is an online homework tool used by Clarkson University.
	
	Copyright 2017-2018 Clarkson University.
	
	This file is part of Autograder.
	
	This program is licensed under the GNU General Purpose License version 3.
	
	Autograder is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	
	Autograder is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	
	You should have received a copy of the GNU General Public License
	along with Autograder. If not, see <http://www.gnu.org/licenses/>.
*/

package edu.clarkson.autograder.client.widgets;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.logging.client.SimpleRemoteLogHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;

import edu.clarkson.autograder.client.Autograder;
import edu.clarkson.autograder.client.objects.Assignment;
import edu.clarkson.autograder.client.objects.Problem;
import edu.clarkson.autograder.client.pages.CoursePage;

public final class AssignmentTreeViewModel implements TreeViewModel {

	public static final SimpleRemoteLogHandler LOG = new SimpleRemoteLogHandler();

	/**
	 * A base class for nodes of this tree. Nodes are possibly top-level
	 * categories, assignments, or problems (leaf).
	 * 
	 * Parameter N is the type of the child to the node.
	 */
	private abstract class SideBarNode<N> {

		protected final ListDataProvider<N> children = new ListDataProvider<N>();
		protected NodeInfo<N> nodeInfo;

		SideBarNode() {
		}

		void addChild(N childNode) {
			children.getList().add(childNode);
			// TODO get add value to History token map
			// contentToken.put(Showcase.getContentWidgetToken(example),
			// example);
		}

		/**
		 * Each node must produce its own label based on its content
		 * 
		 * @return String visible node label
		 */
		abstract String getName();

		/**
		 * Get the node info for the children under this node.
		 * 
		 * @return the node info
		 */
		abstract NodeInfo<N> getNodeInfo();
	}

	/**
	 * A top-level node of the {@link AssignmentTreeViewModel} containing
	 * {@link AssignmentTreeViewModel#AssignmentNode}s. The constructor argument
	 * is the CategoryNode node name which will be displayed to the user.
	 */
	private final class CategoryNode extends SideBarNode<AssignmentNode> {

		private final String name;
		private int size;

		CategoryNode(String name) {
			this.name = name;
			size = 0;
		}

		@Override
		void addChild(AssignmentNode childNode) {
			super.addChild(childNode);
			size++;
		}

		String getName() {
			return name;
		}

		int getSize() {
			return size;
		}

		/**
		 * Get the node info for the children under this node.
		 * 
		 * @return the node info
		 */
		@Override
		NodeInfo<AssignmentNode> getNodeInfo() {
			if (nodeInfo == null) {
				nodeInfo = new DefaultNodeInfo<AssignmentNode>(children, assignmentCell);
			}
			return nodeInfo;
		}
	}

	/**
	 * A mid-level node of the {@link AssignmentTreeViewModel} containing
	 * {@link AssignmentTreeViewModel#ProblemNode}s and appearing under
	 * {@link AssignmentTreeViewModel#CategoryNode} nodes. The constructor
	 * argument is the AssignmentNode node name which will be displayed to the
	 * user.
	 */
	private final class AssignmentNode extends SideBarNode<ProblemNode> {

		private final Assignment assignment;

		AssignmentNode(Assignment assignment) {
			this.assignment = assignment;
		}

		String getName() {
			return assignment.getTitle();
		}

		Date getDueDate() {
			return assignment.getDueDate();
		}
		
		double getTotalPoints() {
			return assignment.getTotalPoints();
		}
		
		double getEarnedPoints() {
			return assignment.getEarnedPoints();
		}

		/**
		 * Get the node info for the children under this node.
		 * 
		 * @return the node info
		 */
		@Override
		NodeInfo<ProblemNode> getNodeInfo() {
			if (nodeInfo == null) {
				nodeInfo = new DefaultNodeInfo<ProblemNode>(children, problemCell, selectionModel, null);
			}
			return nodeInfo;
		}
	}

	private final class ProblemNode {

		private Problem problem;

		ProblemNode(Problem problem) {
			this.problem = problem;
		}

		String getName() {
			return problem.getTitle();
		}
		
		Problem getProblem() {
			return problem;
		}
	}

	/**
	 * The cell used to render {@link AssignmentTreeViewModel#CategoryNode}s.
	 */
	private static final class CategoryCell extends AbstractCell<CategoryNode> {
		@Override
		public void render(Context context, CategoryNode value, SafeHtmlBuilder sb) {
			if (value != null) {
				sb.appendEscaped(value.getName() + " (" + value.getSize() + ")");
			}
		}
	}

	/**
	 * The cell used to render {@link AssignmentTreeViewModel#AssignmentNode}s.
	 */
	private static final class AssignmentCell extends AbstractCell<AssignmentNode> {
		@Override
		public void render(Context context, AssignmentNode value, SafeHtmlBuilder sb) {
			if (value != null) {

				// Assignment Title
				String assignmentTitleCssStyle = "font-size:22px";
				sb.appendHtmlConstant("<span style=\"" + assignmentTitleCssStyle + ";\">")
						.appendEscaped(value.getName()).appendHtmlConstant("</span>");

				sb.appendHtmlConstant("<br>");

				// Due date
				String datePattern = "EEE, M/dd, h:mm a";
				String dueDate = "Due: " + DateTimeFormat.getFormat(datePattern).format(value.getDueDate());
				String dueDateCssStyle = "font-size:16px";
				sb.appendHtmlConstant("<div style=\"" + dueDateCssStyle + ";\">").appendEscaped(dueDate)
						.appendHtmlConstant("</div>");

				// Grade string: 00.00/00.00 (00.00%)
				String gradeString = Autograder.formatGrade(value.getEarnedPoints(), value.getTotalPoints(), 2);

				String gradeCssStyle = "font-size:16px";
				sb.appendHtmlConstant("<div style=\"" + gradeCssStyle + ";\">").appendEscaped(gradeString)
						.appendHtmlConstant("</div>");
			}
		}
	}

	/**
	 * The cell used to render {@link AssignmentTreeViewModel#ProblemNode}s.
	 */
	private static final class ProblemCell extends AbstractCell<ProblemNode> {
		@Override
		public void render(Context context, ProblemNode value, SafeHtmlBuilder sb) {
			if (value != null) {
				sb.appendEscaped(value.getName());
			}
		}
	}

	/**
	 * The top level categories.
	 */
	private final ListDataProvider<CategoryNode> topLevelTreeData;

	/**
	 * The cell used to render {@link AssignmentTreeViewModel#CategoryNodes}s.
	 */
	private static final CategoryCell categoryCell = new CategoryCell();

	/**
	 * The cell used to render {@link AssignmentTreeViewModel#AssignmentNode}s.
	 */
	private static final AssignmentCell assignmentCell = new AssignmentCell();

	/**
	 * The cell used to render {@link AssignmentTreeViewModel#ProblemNode}s.
	 */
	private static final ProblemCell problemCell = new ProblemCell();

	/**
	 * The selection model used to select
	 * {@link AssignmentTreeViewModel#ProblemNode}s.
	 */
	private static final SingleSelectionModel<ProblemNode> selectionModel = new SingleSelectionModel<ProblemNode>();

	public AssignmentTreeViewModel(final SortedMap<Assignment, List<Problem>> treeData,
	        final CoursePage.ProblemSelectionCallback selectionCallback) {
		final List<CategoryNode> preparedData;
		preparedData = initializeTree(treeData);
		topLevelTreeData = new ListDataProvider<CategoryNode>(preparedData);
		
		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			public void onSelectionChange(SelectionChangeEvent event) {
				Problem selected = selectionModel.getSelectedObject().getProblem();
				if (selected != null) {
					selectionCallback.requestSelectedProblemDataAsync(selected.getId());
				}
			}
		});
	}

	/**
	 * Get the {@link NodeInfo} that provides the children of the specified
	 * value.
	 */
	@Override
	public <T> NodeInfo<?> getNodeInfo(T value) {
		if (value == null) {
			// Return the top-level categories
			return new DefaultNodeInfo<CategoryNode>(topLevelTreeData, categoryCell);
		} else if (value instanceof CategoryNode) {
			CategoryNode categoryNode = (CategoryNode) value;
			return categoryNode.getNodeInfo();
		} else if (value instanceof AssignmentNode) {
			AssignmentNode assignmentNode = (AssignmentNode) value;
			return assignmentNode.getNodeInfo();
		}
		return null;
	}

	/**
	 * Check if the specified value represents a leaf node. Leaf nodes cannot be
	 * opened.
	 */
	@Override
	public boolean isLeaf(Object value) {
		return value != null && (value instanceof ProblemNode);
	}

	/**
	 * Initialize the top level categories in the tree.
	 */
	private final List<CategoryNode> initializeTree(final SortedMap<Assignment, List<Problem>> treeData) {

		// method converts treeData (input) to preparedData (output)
		final List<CategoryNode> preparedData = new ArrayList<CategoryNode>();

		// Create top-level: CategoryNodes
		CategoryNode currentAssignmentCategory = new CategoryNode("Current Assignments");
		CategoryNode pastAssignmentCategory = new CategoryNode("Past Assignments");
		preparedData.add(currentAssignmentCategory);
		preparedData.add(pastAssignmentCategory);

		// Create second-tier AssignmentNodes and map them to bottom-tier
		// ProblemNodes
		Date previousAssignmentDueDate = new Date(0); // long ago
		for (SortedMap.Entry<Assignment, List<Problem>> entry : treeData.entrySet()) {

			AssignmentNode assignmentNode = new AssignmentNode(entry.getKey());

			for (Problem problem : entry.getValue()) {
				assignmentNode.addChild(new ProblemNode(problem));
			}

			// Assignment due dates inside the pre-sorted treeData progress
			// forward in time beginning with the near-future. Once due dates
			// jump backwards, the category split point has been found and from
			// then due dates progress backwards in time.
			if (assignmentNode.getDueDate().compareTo(previousAssignmentDueDate) > 0) {
				currentAssignmentCategory.addChild(assignmentNode);
			} else {
				pastAssignmentCategory.addChild(assignmentNode);
			}

			previousAssignmentDueDate = assignmentNode.getDueDate();
		}

		return preparedData;
	}
}