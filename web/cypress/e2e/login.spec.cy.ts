const dsl = {
  inputs: {
    username: () => cy.findByRole("textbox", { name: "username" }),
    password: () => cy.findByPlaceholderText("Password"),
  },
  buttons: {
    login: () => cy.findByRole("button", { name: "Log in" }),
  },
  links: {
    "forgot-password": () =>
      cy.findByRole("link", { name: "Forgot password?" }),
    "create-user": () => cy.findByRole("link", { name: "Create" }),
  },
  alerts: {
    username: () => cy.findByRole("alert", { name: "username-error" }),
    password: () => cy.findByRole("alert", { name: "password-error" }),
  },
};

describe("Login page tests", () => {
  beforeEach(() => cy.visit("/"));

  it("Base URL should redirect to login", () => {
    cy.url().should("include", "/login");
  });

  it("Login page should match snapshot", () => {
    cy.get(".modal").compareSnapshot("loginPage");
  });

  it("Login page with errors should match snapshot", () => {
    dsl.buttons.login().click();
    cy.get(".modal").compareSnapshot("loginPage-errors");
  });

  it("Clicking login without input should show errors", () => {
    dsl.buttons.login().click();
    dsl.alerts.username().contains("username is required");
    dsl.alerts.password().contains("password is required");
  });

  it("Login should redirect to default page", () => {
    dsl.inputs.username().type("jdeveloper");
    dsl.inputs.password().type("test");
    dsl.buttons.login().click();
    cy.url().should("include", "/search");
  });

  it("Clicking forgot password should direct to the registration page", () => {
    dsl.links["forgot-password"]().click();
    cy.url().should("include", "/register");
  });

  it("Clicking create user should direct to the registration page", () => {
    dsl.links["create-user"]().click();
    cy.url().should("include", "/register");
  });

  it("Authentication error should show in the toast component", () => {
    dsl.inputs.username().type("teeest");
    dsl.inputs.password().type("teeeest");
    dsl.buttons.login().click();
    cy.findByRole("alert");
    cy.findByRole("alert", { name: /toast-error\d*/g });
    cy.findByText("Username or password is incorrect");
  });
});
