package com.VaadinTennisTournaments.application.views.list;

import com.VaadinTennisTournaments.application.data.entity.User.UserRanking;
import com.VaadinTennisTournaments.application.data.service.MainService;
import com.VaadinTennisTournaments.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.security.PermitAll;

@Component
@Scope("prototype")
@Route(value = "UserRanking", layout = MainLayout.class)
@PageTitle("Users Ranking | Vaadin Tennis Tournaments")
@PermitAll
public class UserRankingView extends VerticalLayout {
    Grid<UserRanking> grid = new Grid<>(UserRanking.class);
    TextField filterText = new TextField();
    UserRankingForm form;
    MainService mainService;

    public UserRankingView(MainService mainService) {
        this.mainService = mainService;
        addClassName("user-view");
        setSizeFull();
        configureGrid();
        configureForm();

        add(getToolbar(), getContent());
        updateList();
        closeEditor();
    }

    private HorizontalLayout getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.addClassNames("content");
        content.setSizeFull();
        return content;
    }

private void configureForm() {
    form = new UserRankingForm(mainService.findAllInterests(), mainService.findAllUsers(""));
    form.setWidth("25em");
    form.setHeight("40em");
    form.addListener(UserRankingForm.SaveEvent.class, this::saveUserRanking);
    form.addListener(UserRankingForm.DeleteEvent.class, this::deleteUserRanking);
    form.addListener(UserRankingForm.CloseEvent.class, e -> closeEditor());
}

    private void configureGrid() {
        grid.addClassNames("user-grid");
        grid.setSizeFull();
        grid.setColumns("tournamentsNumber", "points");
        grid.addColumn(userRanking -> userRanking.getInterest().getName()).setHeader("Interest");
        grid.addColumn(userRanking -> userRanking.getUser().getNickname()).setHeader("User");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(event ->
            editUserRanking(event.getValue()));
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Filter data...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addUserButton = new Button("Add ranking for user ");
        addUserButton.addClickListener(click -> addUserRanking());
        addUserButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY,ButtonVariant.LUMO_SUCCESS);


        HorizontalLayout toolbar = new HorizontalLayout(filterText, addUserButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void saveUserRanking(UserRankingForm.SaveEvent event) {
        mainService.saveUserRanking(event.getUserRanking());
        updateList();
        closeEditor();
    }

    private void deleteUserRanking(UserRankingForm.DeleteEvent event) {
        mainService.deleteUserRanking(event.getUserRanking());
        updateList();
        closeEditor();
    }

    public void editUserRanking(UserRanking userRanking) {
        if (userRanking == null) {
            closeEditor();
        } else {
            form.setUserRanking(userRanking);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void addUserRanking() {
        grid.asSingleSelect().clear();
        editUserRanking(new UserRanking());
    }

    private void closeEditor() {
        form.setUserRanking(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void updateList() {
        grid.setItems(mainService.findAllUserRankings(filterText.getValue()));
    }
}