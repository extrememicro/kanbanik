package com.googlecode.kanbanik.client.managers;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Image;
import com.googlecode.kanbanik.client.KanbanikResources;
import com.googlecode.kanbanik.dto.UserDto;

public class UsersManager {

	private static final UsersManager INSTANCE = new UsersManager();

	private List<UserDto> users;

	private static final Image defaultPicture = new Image(
			KanbanikResources.INSTANCE.noUserPicture());

	public static UsersManager getInstance() {
		return INSTANCE;
	}

	public void initUsers(List<UserDto> users) {
		this.users = users;
	}

	public List<UserDto> getUsers() {
		if (users == null) {
			return new ArrayList<UserDto>();
		}
		return users;
	}

	public Image getPictureFor(UserDto user) {
		if (user.getPictureUrl() == null) {
			return defaultPicture;
		}

		Image picture = new Image();
		picture.setVisible(false);
		picture.addLoadHandler(new PictureResizingLoadHandler(picture));
		picture.setUrl(user.getPictureUrl());
		Style style = picture.getElement().getStyle();
		style.setBorderStyle(BorderStyle.SOLID);
		style.setBorderWidth(1, Unit.PX);

		return picture;
	}

}