package com.VaadinTennisTournaments.application.views.list;

import com.VaadinTennisTournaments.application.data.entity.ATP.ATP;
import com.VaadinTennisTournaments.application.data.entity.Register.RegisterUser;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

public class RegistrationForm extends FormLayout {

  private RegisterUser registerUser;
  private TextField username = new TextField("Username");

  private EmailField email = new EmailField("Email");

  private PasswordField password =  new PasswordField("Password");

  Binder<RegisterUser> binder = new BeanValidationBinder<>(RegisterUser.class);

  Button save = new Button("Join the community");
  Button delete = new Button("Delete");
  Button close = new Button("Cancel");

  public RegistrationForm() {

    addClassName("Register-form");
    binder.bindInstanceFields(this);
    add(
            username,
            password,
            email,
            save,
        createButtonsLayout());
  }
  private HorizontalLayout createButtonsLayout() {
    save.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

    save.addClickShortcut(Key.ENTER);

    save.addClickListener(event -> validateAndSave());
    delete.addClickListener(event -> fireEvent(new DeleteEvent(this, registerUser)));
    close.addClickListener(event -> fireEvent(new CloseEvent(this) {


    }));

    binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

    return new HorizontalLayout(save);
  }

  public void setRegisterUser(RegisterUser registerUser) {
    this.registerUser = registerUser;
    binder.readBean(registerUser);
  }

  private void validateAndSave() {
    try {
      binder.writeBean(registerUser);
      fireEvent(new SaveEvent(this, registerUser));
    } catch (ValidationException e) {
      e.printStackTrace();
    }
  }

  public static abstract class RegisterUserFormEvent extends ComponentEvent<RegistrationForm> {
    private RegisterUser registerUser;

    protected RegisterUserFormEvent(RegistrationForm source, RegisterUser registerUser) {
      super(source, false);
      this.registerUser = registerUser;
    }

    public RegisterUser getRegisterUser() {
      return registerUser;
    }
  }

  public static class SaveEvent extends RegisterUserFormEvent{
    SaveEvent(RegistrationForm source, RegisterUser registerUser) {
      super(source, registerUser);
    }
  }

  public static class DeleteEvent extends RegistrationForm.RegisterUserFormEvent {
    DeleteEvent(RegistrationForm source , RegisterUser registerUser) {
      super(source, registerUser);
    }

  }

  public static class CloseEvent extends RegistrationForm.RegisterUserFormEvent {
    CloseEvent(RegistrationForm source) {
      super(source, null);
    }
  }
  public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                ComponentEventListener<T> listener) {
    return getEventBus().addListener(eventType, listener);
  }
}