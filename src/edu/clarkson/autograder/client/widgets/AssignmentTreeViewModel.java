package edu.clarkson.autograder.client.widgets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.logging.client.SimpleRemoteLogHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;

import edu.clarkson.autograder.client.objects.Assignment;
import edu.clarkson.autograder.client.objects.Problem;

public class AssignmentTreeViewModel implements TreeViewModel {
	

	public static final SimpleRemoteLogHandler LOG = new SimpleRemoteLogHandler();

	/**
	 * A base class for nodes of this tree. Nodes are possibly top-level
	 * categories, assignments, or problems (leaf).
	 * 
	 * Parameter N is the type of the child to the node.
	 */
	public abstract class SideBarNode<N> {

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
	 * {@link AssignmentTreeViewModel#AssignmentNode}s. The constructor argument is
	 * the CategoryNode node name which will be displayed to the user.
	 */
	public class CategoryNode extends SideBarNode<AssignmentNode> {

		private final String name;

		public CategoryNode(String name) {
			this.name = name;
		}

		String getName() {
			return name;
		}

		/**
		 * Get the node info for the children under this node.
		 * 
		 * @return the node info
		 */
		@Override
		public NodeInfo<AssignmentNode> getNodeInfo() {
			if (nodeInfo == null) {
				nodeInfo = new DefaultNodeInfo<AssignmentNode>(children, new AssignmentCell());
			}
			return nodeInfo;
		}
	}

	/**
	 * A mid-level node of the {@link AssignmentTreeViewModel} containing
	 * {@link AssignmentTreeViewModel#ProblemNode}s and appearing under
	 * {@link AssignmentTreeViewModel#CategoryNode} nodes. The constructor argument
	 * is the AssignmentNode node name which will be displayed to the user.
	 */
	private class AssignmentNode extends SideBarNode<ProblemNode> {

		private final Assignment assignment;

		public AssignmentNode(Assignment assignment) {
			this.assignment = assignment;
		}

		String getName() {
			return assignment.getTitle();
		}

		/**
		 * Get the node info for the children under this node.
		 * 
		 * @return the node info
		 */
		@Override
		public NodeInfo<ProblemNode> getNodeInfo() {
			if (nodeInfo == null) {
				nodeInfo = new DefaultNodeInfo<ProblemNode>(children, problemCell, selectionModel,
				        null);
			}
			return nodeInfo;
		}
	}

	private class ProblemNode {

		private Problem problem;

		public ProblemNode(Problem problem) {
			this.problem = problem;
		}

		String getName() {
			return problem.getTitle();
		}
	}

	/**
	 * The cell used to render {@link AssignmentTreeViewModel#CategoryNode}s.
	 */
	private static class CategoryCell extends AbstractCell<CategoryNode> {
		@Override
		public void render(Context context, CategoryNode value, SafeHtmlBuilder sb) {
			if (value != null) {
				sb.appendEscaped(value.getName());

				// TODO get number of assignments in this category.
				int numberOfChildren = 0;
				if (value.getName() == "Current Assignments")
					numberOfChildren = 2;
				else
					numberOfChildren = 5;

				sb.appendEscaped(" (" + numberOfChildren + ")");
			}
		}
	}

	/**
	 * The cell used to render {@link AssignmentTreeViewModel#AssignmentNode}s.
	 */
	private static class AssignmentCell extends AbstractCell<AssignmentNode> {
		@Override
		public void render(Context context, AssignmentNode value, SafeHtmlBuilder sb) {
			if (value != null) {
				sb.appendEscaped(value.getName());
				// TODO add information to AssignmentNode Cell...
				// such as at-a-glance progress (% pts. earned / total
				// assignment pts.)
				int percentComplete = Math.min(100, 25 * ((int) (Math.random() * 6)));
				String cssTag = "";
				String submissionDiv = "";
				if (percentComplete == 100) {
					cssTag = "submitted";
					submissionDiv = "Submitted";
				} else {
					cssTag = "notSubmitted";
					submissionDiv = "Not Submitted";
				}

				sb.appendHtmlConstant("<br>");
				sb.appendEscaped(percentComplete + "% Complete");
				sb.appendHtmlConstant("<br><div id=\"").appendEscaped(cssTag).appendHtmlConstant("\">")
				        .appendEscaped(submissionDiv).appendHtmlConstant("</div>");
			}
		}
	}

	/**
	 * The cell used to render {@link AssignmentTreeViewModel#ProblemNode}s.
	 */
	private static class ProblemCell extends AbstractCell<ProblemNode> {
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
	private ListDataProvider<CategoryNode> topLevelTreeData;

	/**
	 * The cell used to render {@link AssignmentTreeViewModel#ProblemNode}s.
	 */
	private final ProblemCell problemCell = new ProblemCell();

	/**
	 * The selection model used to select
	 * {@link AssignmentTreeViewModel#ProblemNode}s.
	 */
	private final SelectionModel<ProblemNode> selectionModel = new SingleSelectionModel<ProblemNode>();

	public AssignmentTreeViewModel(Map<Assignment, List<Problem>> treeData) {
		final List<CategoryNode> preparedData;
		preparedData = initializeTree(treeData);
		topLevelTreeData = new ListDataProvider<CategoryNode>(preparedData);
	}

	/**
	 * Get the {@link NodeInfo} that provides the children of the specified
	 * value.
	 */
	@Override
	public <T> NodeInfo<?> getNodeInfo(T value) {
		if (value == null) {
			// Return the top-level categories
			return new DefaultNodeInfo<CategoryNode>(topLevelTreeData, new CategoryCell());
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
	public List<CategoryNode> initializeTree(Map<Assignment, List<Problem>> treeData) {
		List<CategoryNode> preparedData = new ArrayList<CategoryNode>();

		// Current Assignments
		{
			CategoryNode categoryNode = new CategoryNode("Current Assignments");
			preparedData.add(categoryNode);


			// TODO temporary: need async load of assignments
			AssignmentNode a1 = new AssignmentNode(new Assignment(1, 1, "Assignment 1", "dueDate"));
			a1.addChild(new ProblemNode(new Problem(1, 1, "Question 4.2", 20, 17.2)));
			a1.addChild(new ProblemNode(new Problem(1, 1, "Question 4.3", 20, 17.2)));
			a1.addChild(new ProblemNode(new Problem(1, 1, "Question 4.7", 20, 17.2)));
			a1.addChild(new ProblemNode(new Problem(1, 1, "Question 8.2", 20, 17.2)));
			a1.addChild(new ProblemNode(new Problem(1, 1, "Question 9.2", 20, 10.5)));
			a1.addChild(new ProblemNode(new Problem(1, 1, "Question 11.5", 20, 17.2)));
			a1.addChild(new ProblemNode(new Problem(1, 1, "Question 15.2", 20, 17.2)));
			a1.addChild(new ProblemNode(new Problem(1, 1, "Question 19.6", 20, 17.2)));
			categoryNode.addChild(a1);

			AssignmentNode a2 = new AssignmentNode(new Assignment(2, 1, "Assignment 2", "dueDate"));
			a2.addChild(new ProblemNode(new Problem(1, 1, "Question 4.2", 20, 17.2)));
			a2.addChild(new ProblemNode(new Problem(1, 1, "Question 4.3", 20, 17.2)));
			a2.addChild(new ProblemNode(new Problem(1, 1, "Question 4.7", 20, 17.2)));
			a2.addChild(new ProblemNode(new Problem(1, 1, "Question 8.2", 20, 17.2)));
			a2.addChild(new ProblemNode(new Problem(1, 1, "Question 9.2", 20, 10.5)));
			a2.addChild(new ProblemNode(new Problem(1, 1, "Question 11.5", 20, 17.2)));
			a2.addChild(new ProblemNode(new Problem(1, 1, "Question 15.2", 20, 17.2)));
			a2.addChild(new ProblemNode(new Problem(1, 1, "Question 19.6", 20, 17.2)));
			categoryNode.addChild(a2);
		}

		// Past Assignments
		{
			CategoryNode categoryNode = new CategoryNode("Past Assignments");
			preparedData.add(categoryNode);

			AssignmentNode a0 = new AssignmentNode(new Assignment(2, 1, "Assignment 3", "dueDate"));
			a0.addChild(new ProblemNode(new Problem(1, 1, "Question 9.2", 20, 10.5)));
			a0.addChild(new ProblemNode(new Problem(1, 1, "Question 11.5", 20, 17.2)));
			categoryNode.addChild(a0);

			AssignmentNode a1 = new AssignmentNode(new Assignment(2, 1, "Assignment 27", "dueDate"));
			a1.addChild(new ProblemNode(new Problem(1, 1, "Question 9.2", 20, 10.5)));
			a1.addChild(new ProblemNode(new Problem(1, 1, "Question 11.5", 20, 17.2)));
			categoryNode.addChild(a1);

			AssignmentNode a2 = new AssignmentNode(new Assignment(2, 1, "Assignment 28", "dueDate"));
			a2.addChild(new ProblemNode(new Problem(1, 1, "Question 9.2", 20, 10.5)));
			a2.addChild(new ProblemNode(new Problem(1, 1, "Question 11.5", 20, 17.2)));
			categoryNode.addChild(a2);

			AssignmentNode a3 = new AssignmentNode(new Assignment(2, 1, "Assignment 92", "dueDate"));
			a3.addChild(new ProblemNode(new Problem(1, 1, "Question 9.2", 20, 10.5)));
			a3.addChild(new ProblemNode(new Problem(1, 1, "Question 11.5", 20, 17.2)));
			categoryNode.addChild(a3);

			AssignmentNode a4 = new AssignmentNode(new Assignment(2, 1, "Assignment 2.2b", "dueDate"));
			a4.addChild(new ProblemNode(new Problem(1, 1, "Question 9.2", 20, 10.5)));
			a4.addChild(new ProblemNode(new Problem(1, 1, "Question 11.5", 20, 17.2)));
			categoryNode.addChild(a4);
		}

		return preparedData;
	}
}