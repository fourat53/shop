SETUP:
- Replace ${DATABASE_URL}, ${DATABASE_USERNAME}, ${DATABASE_PASSWORD} with your database credentials. I'm using a production-ready Neon database.

NOTES:
# Retrieves all cart items.
GET /cartItems
# Sets the maximum number of items returned per page to 5.
GET /cartItems?size=5            
# Retrieves the 3rd page of data (uses zero-based indexing: 0, 1, 2).
GET /cartItems?page=2            
# Sorts the records by the 'name' field in descending order (Z to A).
GET /cartItems?sort=name,desc    
# Transforms the JSON output structure to match a custom 'brief' interface.
GET /cartItems?projection=brief  

# Retrieves a specific cart item by its ID.
GET /cartItems/{id}

# Creates a new cart item.
POST /cartItems

# Updates an existing cart item by its ID.
PUT /cartItems/{id}

# Partially updates an existing cart item by its ID.
PATCH /cartItems/{id}

# Deletes a cart item by its ID.
DELETE /cartItems/{id}

IMPORTANT:
- You can use the following query parameters above. This works for all endpoints (products, categories, cartItems, etc) except if exclude the endpoint in the entity's repository.
- Exclusion example:
  @RepositoryRestResource
  public interface CategoryRepository extends JpaRepository<Category, Long> {
  @Override
  @RestResource(exported = false)
    void deleteById(Long id);
  }