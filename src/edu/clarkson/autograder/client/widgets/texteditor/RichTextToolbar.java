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

/*
 * Copyright 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * 
 * Adapted source for use with Clarkson University Autograder project.
 * Separated Image icons from this Java source file: annotated images with @Source to complete the reference
 * Removed deprecated references to RichTextArea.BasicFormatter and RichTextArea.ExtendedFormatter in favor of RichTextArea.Formatter.
 */
package edu.clarkson.autograder.client.widgets.texteditor;

import java.lang.annotation.Annotation;
import java.lang.annotation.Target;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.i18n.client.Constants;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A sample toolbar for use with {@link RichTextArea}. It provides a simple UI
 * for all rich text formatting, dynamically displayed only for the available
 * functionality.
 */
public class RichTextToolbar extends Composite {

	/**
	 * This {@link ClientBundle} is used for all the button icons. Using a
	 * bundle allows all of these images to be packed into a single image, which
	 * saves a lot of HTTP requests, drastically improving startup time.
	 */
	public interface Images extends ClientBundle {
		
		@Source("icon/bold.gif")
		ImageResource bold();
		
		@Source("icon/createLink.gif")
		ImageResource createLink();
		
		@Source("icon/hr.gif")
		ImageResource hr();
		
		@Source("icon/indent.gif")
		ImageResource indent();

		@Source("icon/insertImage.gif")
		ImageResource insertImage();
		
		@Source("icon/italic.gif")
		ImageResource italic();

		@Source("icon/justifyCenter.gif")
		ImageResource justifyCenter();

		@Source("icon/justifyLeft.gif")
		ImageResource justifyLeft();

		@Source("icon/justifyRight.gif")
		ImageResource justifyRight();

		@Source("icon/ol.gif")
		ImageResource ol();

		@Source("icon/outdent.gif")
		ImageResource outdent();

		@Source("icon/removeFormat.gif")
		ImageResource removeFormat();

		@Source("icon/removeLink.gif")
		ImageResource removeLink();

		@Source("icon/strikeThrough.gif")
		ImageResource strikeThrough();

		@Source("icon/subscript.gif")
		ImageResource subscript();

		@Source("icon/superscript.gif")
		ImageResource superscript();

		@Source("icon/ul.gif")
		ImageResource ul();

		@Source("icon/underline.gif")
		ImageResource underline();
		
	}

	/**
	 * This {@link Constants} interface is used to make the toolbar's strings
	 * internationalizable.
	 */
	public interface Strings extends Constants {
		
		
		String black();

		String blue();

		String bold();

		String color();

		String createLink();

		String font();

		String green();

		String hr();

		String indent();

		String insertImage();

		String italic();

		String justifyCenter();

		String justifyLeft();

		String justifyRight();

		String large();

		String medium();

		String normal();

		String ol();

		String outdent();

		String red();

		String removeFormat();

		String removeLink();

		String size();

		String small();

		String strikeThrough();

		String subscript();

		String superscript();

		String ul();

		String underline();

		String white();

		String xlarge();

		String xsmall();

		String xxlarge();

		String xxsmall();

		String yellow();
	}

	/**
	 * We use an inner EventHandler class to avoid exposing event methods on the
	 * RichTextToolbar itself.
	 */
	private class EventHandler implements ClickHandler, ChangeHandler, KeyUpHandler {

		public void onChange(ChangeEvent event) {
			Widget sender = (Widget) event.getSource();

			if (sender == backColors) {
				formatter.setBackColor(backColors.getValue(backColors.getSelectedIndex()));
				backColors.setSelectedIndex(0);
			} else if (sender == foreColors) {
				formatter.setForeColor(foreColors.getValue(foreColors.getSelectedIndex()));
				foreColors.setSelectedIndex(0);
			} else if (sender == fonts) {
				formatter.setFontName(fonts.getValue(fonts.getSelectedIndex()));
				fonts.setSelectedIndex(0);
			} else if (sender == fontSizes) {
				formatter.setFontSize(fontSizesConstants[fontSizes.getSelectedIndex() - 1]);
				fontSizes.setSelectedIndex(0);
			}
		}

		public void onClick(ClickEvent event) {
			Widget sender = (Widget) event.getSource();

			if (sender == bold) {
				formatter.toggleBold();
			} else if (sender == italic) {
				formatter.toggleItalic();
			} else if (sender == underline) {
				formatter.toggleUnderline();
			} else if (sender == subscript) {
				formatter.toggleSubscript();
			} else if (sender == superscript) {
				formatter.toggleSuperscript();
			} else if (sender == strikethrough) {
				formatter.toggleStrikethrough();
			} else if (sender == indent) {
				formatter.rightIndent();
			} else if (sender == outdent) {
				formatter.leftIndent();
			} else if (sender == justifyLeft) {
				formatter.setJustification(RichTextArea.Justification.LEFT);
			} else if (sender == justifyCenter) {
				formatter.setJustification(RichTextArea.Justification.CENTER);
			} else if (sender == justifyRight) {
				formatter.setJustification(RichTextArea.Justification.RIGHT);
			} else if (sender == insertImage) {
				String url = Window.prompt("Enter an image URL:", "http://");
				if (url != null) {
					formatter.insertImage(url);
				}
			} else if (sender == createLink) {
				String url = Window.prompt("Enter a link URL:", "http://");
				if (url != null) {
					formatter.createLink(url);
				}
			} else if (sender == removeLink) {
				formatter.removeLink();
			} else if (sender == hr) {
				formatter.insertHorizontalRule();
			} else if (sender == ol) {
				formatter.insertOrderedList();
			} else if (sender == ul) {
				formatter.insertUnorderedList();
			} else if (sender == removeFormat) {
				formatter.removeFormat();
			} else if (sender == richText) {
				// We use the RichTextArea's onKeyUp event to update the toolbar
				// status.
				// This will catch any cases where the user moves the cursur
				// using the
				// keyboard, or uses one of the browser's built-in keyboard
				// shortcuts.
				updateStatus();
			}
		}

		public void onKeyUp(KeyUpEvent event) {
			Widget sender = (Widget) event.getSource();
			if (sender == richText) {
				// We use the RichTextArea's onKeyUp event to update the toolbar
				// status.
				// This will catch any cases where the user moves the cursur
				// using the
				// keyboard, or uses one of the browser's built-in keyboard
				// shortcuts.
				updateStatus();
			}
		}
	}

	private static final RichTextArea.FontSize[] fontSizesConstants = new RichTextArea.FontSize[] {
	        RichTextArea.FontSize.XX_SMALL, RichTextArea.FontSize.X_SMALL, RichTextArea.FontSize.SMALL,
	        RichTextArea.FontSize.MEDIUM, RichTextArea.FontSize.LARGE, RichTextArea.FontSize.X_LARGE,
	        RichTextArea.FontSize.XX_LARGE };

	private Images images = (Images) GWT.create(Images.class);
	private Strings strings = (Strings) GWT.create(Strings.class);
	private EventHandler handler = new EventHandler();

	private RichTextArea richText;
	private RichTextArea.Formatter formatter;

	private VerticalPanel outer = new VerticalPanel();
	private HorizontalPanel topPanel = new HorizontalPanel();
	private HorizontalPanel bottomPanel = new HorizontalPanel();
	private ToggleButton bold;
	private ToggleButton italic;
	private ToggleButton underline;
	private ToggleButton subscript;
	private ToggleButton superscript;
	private ToggleButton strikethrough;
	private PushButton indent;
	private PushButton outdent;
	private PushButton justifyLeft;
	private PushButton justifyCenter;
	private PushButton justifyRight;
	private PushButton hr;
	private PushButton ol;
	private PushButton ul;
	private PushButton insertImage;
	private PushButton createLink;
	private PushButton removeLink;
	private PushButton removeFormat;

	private ListBox backColors;
	private ListBox foreColors;
	private ListBox fonts;
	private ListBox fontSizes;

	/**
	 * Creates a new toolbar that drives the given rich text area.
	 * 
	 * @param richText
	 *            the rich text area to be controlled
	 */
	public RichTextToolbar(RichTextArea richText) {
		this.richText = richText;
		this.formatter = richText.getFormatter();

		outer.add(topPanel);
		outer.add(bottomPanel);
		topPanel.setWidth("100%");
		bottomPanel.setWidth("100%");

		initWidget(outer);
		setStyleName("gwt-RichTextToolbar");
		richText.addStyleName("hasRichTextToolbar");

		// top row
		if (formatter != null) {
			// basic
			topPanel.add(bold = createToggleButton(images.bold(), strings.bold()));
			topPanel.add(italic = createToggleButton(images.italic(), strings.italic()));
			topPanel.add(underline = createToggleButton(images.underline(), strings.underline()));
			topPanel.add(subscript = createToggleButton(images.subscript(), strings.subscript()));
			topPanel.add(superscript = createToggleButton(images.superscript(), strings.superscript()));
			topPanel.add(justifyLeft = createPushButton(images.justifyLeft(), strings.justifyLeft()));
			topPanel.add(justifyCenter = createPushButton(images.justifyCenter(), strings.justifyCenter()));
			topPanel.add(justifyRight = createPushButton(images.justifyRight(), strings.justifyRight()));

			// extended
			topPanel.add(strikethrough = createToggleButton(images.strikeThrough(), strings.strikeThrough()));
			topPanel.add(indent = createPushButton(images.indent(), strings.indent()));
			topPanel.add(outdent = createPushButton(images.outdent(), strings.outdent()));
			topPanel.add(hr = createPushButton(images.hr(), strings.hr()));
			topPanel.add(ol = createPushButton(images.ol(), strings.ol()));
			topPanel.add(ul = createPushButton(images.ul(), strings.ul()));
			topPanel.add(insertImage = createPushButton(images.insertImage(), strings.insertImage()));
			topPanel.add(createLink = createPushButton(images.createLink(), strings.createLink()));
			topPanel.add(removeLink = createPushButton(images.removeLink(), strings.removeLink()));
			topPanel.add(removeFormat = createPushButton(images.removeFormat(), strings.removeFormat()));
		}

		// bottom row
		if (formatter != null) {
			bottomPanel.add(backColors = createColorList("Background"));
			bottomPanel.add(foreColors = createColorList("Foreground"));
			bottomPanel.add(fonts = createFontList());
			bottomPanel.add(fontSizes = createFontSizes());

			// We only use these handlers for updating status, so don't hook
			// them up
			// unless at least basic editing is supported.
			richText.addKeyUpHandler(handler);
			richText.addClickHandler(handler);
		}
	}

	private ListBox createColorList(String caption) {
		ListBox lb = new ListBox();
		lb.addChangeHandler(handler);
		lb.setVisibleItemCount(1);

		lb.addItem(caption);
		lb.addItem(strings.white(), "white");
		lb.addItem(strings.black(), "black");
		lb.addItem(strings.red(), "red");
		lb.addItem(strings.green(), "green");
		lb.addItem(strings.yellow(), "yellow");
		lb.addItem(strings.blue(), "blue");
		return lb;
	}

	private ListBox createFontList() {
		ListBox lb = new ListBox();
		lb.addChangeHandler(handler);
		lb.setVisibleItemCount(1);

		lb.addItem(strings.font(), "");
		lb.addItem(strings.normal(), "");
		lb.addItem("Times New Roman", "Times New Roman");
		lb.addItem("Arial", "Arial");
		lb.addItem("Courier New", "Courier New");
		lb.addItem("Georgia", "Georgia");
		lb.addItem("Trebuchet", "Trebuchet");
		lb.addItem("Verdana", "Verdana");
		return lb;
	}

	private ListBox createFontSizes() {
		ListBox lb = new ListBox();
		lb.addChangeHandler(handler);
		lb.setVisibleItemCount(1);

		lb.addItem(strings.size());
		lb.addItem(strings.xxsmall());
		lb.addItem(strings.xsmall());
		lb.addItem(strings.small());
		lb.addItem(strings.medium());
		lb.addItem(strings.large());
		lb.addItem(strings.xlarge());
		lb.addItem(strings.xxlarge());
		return lb;
	}

	private PushButton createPushButton(ImageResource img, String tip) {
		PushButton pb = new PushButton(new Image(img));
		pb.addClickHandler(handler);
		pb.setTitle(tip);
		return pb;
	}

	private ToggleButton createToggleButton(ImageResource img, String tip) {
		ToggleButton tb = new ToggleButton(new Image(img));
		tb.addClickHandler(handler);
		tb.setTitle(tip);
		return tb;
	}

	/**
	 * Updates the status of all the stateful buttons.
	 */
	private void updateStatus() {
		if (formatter != null) {
			// basic
			bold.setDown(formatter.isBold());
			italic.setDown(formatter.isItalic());
			underline.setDown(formatter.isUnderlined());
			subscript.setDown(formatter.isSubscript());
			superscript.setDown(formatter.isSuperscript());

			// extended
			strikethrough.setDown(formatter.isStrikethrough());
		}
	}
}
