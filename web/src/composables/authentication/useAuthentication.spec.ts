import { useLogin, useRegister, logout } from "./useAuthentication";
import { isUser } from "@/types/user";
import { actions as authenticationStoreActions } from "@/stores/__mocks__/authentication";

vi.mock("@/stores/authentication");

describe("authenticationHttp tests", () => {
  describe("useLogin tests", () => {
    it("Should login and pass username and password should succeed", async () => {
      const username = "jdeveloper";
      const password = "test";

      const loginState = useLogin(() => ({
        username,
        password,
      }));

      const loginResult = await loginState.doLogin();
      expect(isUser(loginResult)).toBe(true);
      expect(authenticationStoreActions.addBasicAuth).toHaveBeenCalledWith(
        username,
        password
      );
    });

    it("Should remove credentials on logout", () => {
      logout();
      expect(authenticationStoreActions.removeAuthToken).toHaveBeenCalled();
    });
  });

  describe("useRegister tests", () => {
    it("Should register new user successfully", async () => {
      const newUsername = "jschmo";
      const newPassword = "test";

      const registerState = useRegister(() => ({
        username: newUsername,
        password: newPassword,
      }));

      const registerResult = await registerState.doRegister();
      expect(registerResult).toBe(true);
    });
  });
});
