package edu.clarkson.autograder.client;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.logging.client.SimpleRemoteLogHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;

import edu.clarkson.autograder.client.services.AssignmentProblemTreeDataService;
import edu.clarkson.autograder.client.services.AssignmentProblemTreeDataServiceAsync;

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
		private final String name;
		protected NodeInfo<N> nodeInfo;

		SideBarNode(String name) {
			this.name = name;
		}

		void addChild(N childNode) {
			children.getList().add(childNode);
			// TODO get add value to History token map
			// contentToken.put(Showcase.getContentWidgetToken(example),
			// example);
		}

		String getName() {
			return name;
		}

		/**
		 * Get the node info for the children under this node.
		 * 
		 * @return the node info
		 */
		abstract NodeInfo<N> getNodeInfo();
	}

	/**
	 * A top-level node of the {@link AssignmentTreeViewModel} containing
	 * {@link AssignmentTreeViewModel#Assignment}s. The constructor argument is
	 * the Category node name which will be displayed to the user.
	 */
	@SuppressWarnings("serial")
	public class Category extends SideBarNode<Assignment> implements Serializable {

		public Category(String name) {
			super(name);
		}

		/**
		 * Get the node info for the children under this node.
		 * 
		 * @return the node info
		 */
		@Override
		public NodeInfo<Assignment> getNodeInfo() {
			if (nodeInfo == null) {
				nodeInfo = new DefaultNodeInfo<Assignment>(children, new AssignmentCell());
			}
			return nodeInfo;
		}
	}

	/**
	 * A mid-level node of the {@link AssignmentTreeViewModel} containing
	 * {@link AssignmentTreeViewModel#ProblemListing}s and appearing under
	 * {@link AssignmentTreeViewModel#Category} nodes. The constructor argument
	 * is the Assignment node name which will be displayed to the user.
	 */
	private class Assignment extends SideBarNode<ProblemListing> {

		public Assignment(String name) {
			super(name);
		}

		/**
		 * Get the node info for the children under this node.
		 * 
		 * @return the node info
		 */
		@Override
		public NodeInfo<ProblemListing> getNodeInfo() {
			if (nodeInfo == null) {
				nodeInfo = new DefaultNodeInfo<ProblemListing>(children, problemListingCell, selectionModel,
				        null);
			}
			return nodeInfo;
		}
	}

	private class ProblemListing {

		private String name;

		public ProblemListing(String name) {
			this.name = name;
		}

		String getName() {
			return name;
		}
	}

	/**
	 * The cell used to render Categories.
	 */
	private static class CategoryCell extends AbstractCell<Category> {
		@Override
		public void render(Context context, Category value, SafeHtmlBuilder sb) {
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
	 * The cell used to render Assignments.
	 */
	private static class AssignmentCell extends AbstractCell<Assignment> {
		@Override
		public void render(Context context, Assignment value, SafeHtmlBuilder sb) {
			if (value != null) {
				sb.appendEscaped(value.getName());
				// TODO add information to Assignment Cell...
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
	 * The cell used to render ProblemListings.
	 */
	private static class ProblemListingCell extends AbstractCell<ProblemListing> {
		@Override
		public void render(Context context, ProblemListing value, SafeHtmlBuilder sb) {
			if (value != null) {
				sb.appendEscaped(value.getName());
			}
		}
	}

	/**
	 * The top level categories.
	 */
	private ListDataProvider<Category> topLevelTreeData = new ListDataProvider<Category>();

	/**
	 * The cell used to render problem listings.
	 */
	private final ProblemListingCell problemListingCell = new ProblemListingCell();

	/**
	 * The selection model used to select problem listings.
	 */
	private final SelectionModel<ProblemListing> selectionModel = new SingleSelectionModel<ProblemListing>();

	public AssignmentTreeViewModel() {
		requestAssignmentProblemTreeDataAsync();
		//intializeTree();
	}

	private void requestAssignmentProblemTreeDataAsync() {
		LOG.publish(new LogRecord(Level.INFO, "AssignmentTreeViewModel#requestAssignmentProblemTreeDataAsync - begin"));
		
		AssignmentProblemTreeDataServiceAsync treeDataService = GWT.create(AssignmentProblemTreeDataService.class);
		treeDataService.fetchTreeData(new AsyncCallback<List<Category>>() {
			@Override
			public void onFailure(Throwable caught) {
				LOG.publish(new LogRecord(Level.INFO,
				        "AssignmentTreeViewModel#requestAssignmentProblemTreeDataAsync - onFailure"));
				//throw new AsyncCallbackException("Server error: failed to retrieve course assignments.");
			}

			@Override
			public void onSuccess(List<Category> treeData) {
				LOG.publish(new LogRecord(Level.INFO,
				        "AssignmentTreeViewModel#requestAssignmentProblemTreeDataAsync - onSuccess"));
				//topLevelTreeData.setList(treeData);
				
				// TODO remove this TEMP DEBUG
				//initializeTree();
			}
		});
		LOG.publish(new LogRecord(Level.INFO, "AssignmentTreeViewModel#requestAssignmentProblemTreeDataAsync - end"));	
	}
	

	/**
	 * Get the {@link NodeInfo} that provides the children of the specified
	 * value.
	 */
	@Override
	public <T> NodeInfo<?> getNodeInfo(T value) {
		if (value == null) {
			// Return the top-level categories
			return new DefaultNodeInfo<Category>(topLevelTreeData, new CategoryCell());
		} else if (value instanceof Category) {
			Category category = (Category) value;
			return category.getNodeInfo();
		} else if (value instanceof Assignment) {
			Assignment assignment = (Assignment) value;
			return assignment.getNodeInfo();
		}
		return null;
	}

	/**
	 * Check if the specified value represents a leaf node. Leaf nodes cannot be
	 * opened.
	 */
	@Override
	public boolean isLeaf(Object value) {
		return value != null && (value instanceof ProblemListing);
	}

	/**
	 * Initialize the top level categories in the tree.
	 */
	private void initializeTree() {
		List<Category> catList = topLevelTreeData.getList();

		// Current Assignments
		{
			Category category = new Category("Current Assignments");
			catList.add(category);

			// TODO temporary: need async load of assignments
			Assignment a1 = new Assignment("Assignment 4");
			a1.addChild(new ProblemListing("Question 4.7"));
			a1.addChild(new ProblemListing("Question 4.8"));
			a1.addChild(new ProblemListing("Question 4.15"));
			a1.addChild(new ProblemListing("Question 4.16"));
			a1.addChild(new ProblemListing("Question 4.22"));
			a1.addChild(new ProblemListing("Question 5.1"));
			a1.addChild(new ProblemListing("Question 5.3"));
			category.addChild(a1);

			Assignment a3 = new Assignment("Take Home Exam II");
			a3.addChild(new ProblemListing("Question 1"));
			a3.addChild(new ProblemListing("Question 2"));
			a3.addChild(new ProblemListing("Question 3"));
			a3.addChild(new ProblemListing("Question 4"));
			a3.addChild(new ProblemListing("Question 5"));
			a3.addChild(new ProblemListing("Bonus!!"));
			category.addChild(a3);
		}

		// Past Assignments
		{
			Category category = new Category("Past Assignments");
			catList.add(category);

			Assignment a0 = new Assignment("Pre-test (Quiz) Due 9/31");
			a0.addChild(new ProblemListing("Problem 1"));
			a0.addChild(new ProblemListing("Problem 2"));
			a0.addChild(new ProblemListing("Problem 3"));
			a0.addChild(new ProblemListing("Problem 4"));
			category.addChild(a0);

			Assignment a1 = new Assignment("Assignment 1");
			a1.addChild(new ProblemListing("Problem 1"));
			a1.addChild(new ProblemListing("Problem 2"));
			a1.addChild(new ProblemListing("Problem 3"));
			a1.addChild(new ProblemListing("Problem 4"));
			category.addChild(a1);

			Assignment a2 = new Assignment("Assignment 2");
			a2.addChild(new ProblemListing("Problem 1"));
			a2.addChild(new ProblemListing("Problem 2"));
			a2.addChild(new ProblemListing("Problem 3"));
			category.addChild(a2);

			Assignment a3 = new Assignment("Take Home Exam");
			a3.addChild(new ProblemListing("Question 1"));
			a3.addChild(new ProblemListing("Question 2"));
			a3.addChild(new ProblemListing("Question 3"));
			a3.addChild(new ProblemListing("Question 4"));
			a3.addChild(new ProblemListing("Question 5"));
			a3.addChild(new ProblemListing("Bonus!!"));
			category.addChild(a3);

			Assignment a4 = new Assignment("Assignment 3");
			a4.addChild(new ProblemListing("One"));
			a4.addChild(new ProblemListing("Two"));
			a4.addChild(new ProblemListing("Three"));
			category.addChild(a4);
		}
	}
}