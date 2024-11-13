import * as z from "zod";
import { createContentResponseSchema } from "./response";

const genre = z.enum([
  "Action",
  "Free to Play",
  "Adventure",
  "Massively Multiplayer",
  "RPG",
  "Indie",
  "Simulation",
  "Strategy",
  "Early Access",
  "Racing",
  "Sports",
  "Casual",
  "Movie",
  "Violent",
  "Gore",
  "Audio Production",
  "Education",
  "Software Training",
  "Utilities",
  "Design & Illustration",
]);

export const gameSchema = z.object({
  gid: z.number(),
  gname: z.string(),
  cost: z.string(),
  discountedCost: z.string(),
  url: z.string(),
  description: z.string(),
  genres: z.array(genre),
  franchise: z.string().nullable(),
  releaseDate: z.string(),
  rawgId: z.number(),
  age_rating: z.string(),
});

export type Game = z.infer<typeof gameSchema>;

export const paginatedGameSchema = createContentResponseSchema(
  z.array(gameSchema)
);

export type PaginatedGameResponse = z.infer<typeof paginatedGameSchema>;
