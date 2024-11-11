import { handleError } from "@/services/framework/errorHandler";
import { AppConfigs } from "./mount";

export const configs: AppConfigs = {
  errorHandler: handleError,
};
