import * as z from "zod";

export const paginationDetailsSchema = z.object({
  page: z.number(),
  totalPages: z.number(),
  totalElements: z.number(),
  pageSize: z.number(),
});

export function createContentResponseSchema<ItemType extends z.ZodTypeAny>(
  item: ItemType
) {
  return z.object({
    content: item,
    pagination: paginationDetailsSchema.optional(),
  });
}
