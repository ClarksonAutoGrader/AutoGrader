package edu.clarkson.autograder.client;

import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.TreeViewModel;

public class AssignmentTreeViewModel implements TreeViewModel {

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
	private class Category extends SideBarNode<Assignment> {

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

	/**
	 * A bottom-level leaf of the {@link AssignmentTreeViewModel} appearing
	 * under {@link AssignmentTreeViewModel#Assignment} nodes. The constructor
	 * argument is the ProblemListing name which will be displayed to the user.
	 */
	private class ProblemListing {

		final String name;

		public ProblemListing(String name) {
			this.name = name;
		}

		public String getName() {
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
				int percentComplete = 25 * ((int) (Math.random() * 5));
				sb.appendEscaped(" (" + percentComplete + " % Complete)");
				sb.appendHtmlConstant("<br>");
				sb.appendEscaped("Not Submitted");
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
	private final ListDataProvider<Category> categories = new ListDataProvider<Category>();

	/**
	 * The cell used to render problem listings.
	 */
	private final ProblemListingCell problemListingCell = new ProblemListingCell();

	/**
	 * The selection model used to select examples.
	 */
	private final SelectionModel<ProblemListing> selectionModel;

	public AssignmentTreeViewModel(SelectionModel<ProblemListing> selectionModel) {
		this.selectionModel = selectionModel;
		initializeTree();
	}

	/**
	 * Get the {@link NodeInfo} that provides the children of the specified
	 * value.
	 */
	@Override
	public <T> NodeInfo<?> getNodeInfo(T value) {
		if (value == null) {
			// Return the top-level categories
			return new DefaultNodeInfo<Category>(categories, new CategoryCell());
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
		List<Category> catList = categories.getList();

		// Current Assignments
		{
			Category category = new Category("Current Assignments");
			catList.add(category);

			// TODO temporary: need async load of assignments
			Assignment assignment = new Assignment("First Assignment");
			assignment.addChild(new ProblemListing("First Problem"));

			category.addChild(assignment);
		}

		// Past Assignments
		{
			Category category = new Category("Past Assignments");
			catList.add(category);

			Assignment assignment = new Assignment("Assignment 1");
			assignment.addChild(new ProblemListing("Problem 1"));

			category.addChild(assignment);
		}
	}
}