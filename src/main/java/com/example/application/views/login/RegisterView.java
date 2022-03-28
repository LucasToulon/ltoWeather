package com.example.application.views.login;

import com.example.application.data.service.AuthService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route("register")
public class RegisterView extends Composite {

    private final AuthService authService;

    public RegisterView(AuthService authService) {
        this.authService = authService;
    }

    @Override
    protected Component initContent() {
        TextField username = new TextField("Nutzername");
        PasswordField password1 = new PasswordField("Passwort");
        PasswordField password2 = new PasswordField("Passwort wiederholen");
        return new VerticalLayout(
                new H2("Registrieren"),
                username,
                password1,
                password2,
                new Button("Bestätigen", event -> register(
                        username.getValue(),
                        password1.getValue(),
                        password2.getValue()
                ))
        );
    }

    private void register(String username, String password1, String password2) {
        if (username.trim().isEmpty()) {
            Notification.show("Bitte Nutzernamen eingeben");
        } else if (password1.isEmpty()) {
            Notification.show("Passwort eingeben!");
        } else if (!password1.equals(password2)) {
            Notification.show("Passwörter stimmen nicht überein");
        } else {
            authService.register(username, password1);
            UI.getCurrent().navigate("login");
            Notification.show("Sie sind nun registriert");
        }
    }
}