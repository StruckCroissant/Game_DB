import { getErrorResource } from "mocks/repos/error";
import { endpoint } from "../baseUrls";
import { http, HttpResponse } from "msw";
import games from "../fixtures/games.json";

export const handlers = [
  http.get(endpoint("/game"), async ({ request }): Promise<HttpResponse> => {
    const url = new URL(request.url);
    const name = url.searchParams.get("name");

    if (!name) {
      return getErrorResource({
        title: "Constraint Violation",
        detail: "Search request must contain at least one search criteria",
        status: 400,
        type: "https://zalando.github.io/problem/constraint-violation",
      });
    }

    return HttpResponse.json(games);
  }),
];
