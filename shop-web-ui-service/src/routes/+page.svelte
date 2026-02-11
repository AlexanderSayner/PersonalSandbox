<script>
  import { onMount } from 'svelte';
  
  let featuredProducts = [];
  
  // Simulated featured product data
  const mockProducts = [
    {
      id: 1,
      name: "Wireless Headphones",
      description: "High-quality wireless headphones with noise cancellation",
      price: 129.99,
      category: "electronics",
      image: "https://placehold.co/300x300?text=Headphones"
    },
    {
      id: 2,
      name: "Cotton T-Shirt",
      description: "Comfortable cotton t-shirt for everyday wear",
      price: 19.99,
      category: "clothing",
      image: "https://placehold.co/300x300?text=T-Shirt"
    },
    {
      id: 3,
      name: "Programming Book",
      description: "Learn modern programming techniques and best practices",
      price: 49.99,
      category: "books",
      image: "https://placehold.co/300x300?text=Book"
    },
    {
      id: 4,
      name: "Coffee Maker",
      description: "Automatic coffee maker with timer function",
      price: 89.99,
      category: "home",
      image: "https://placehold.co/300x300?text=Coffee+Maker"
    }
  ];

  onMount(() => {
    // Get first 4 products as featured
    featuredProducts = mockProducts.slice(0, 4);
  });

  function handleBuy(product) {
    alert(`Added ${product.name} to cart!`);
  }
</script>

<h1>Welcome to ShopWeb</h1>
<p class="subtitle">Discover amazing products at great prices</p>

<div class="featured-products">
  <h2>Featured Products</h2>
  <div class="products-grid">
    {#each featuredProducts as product (product.id)}
      <div class="product-card">
        <img src={product.image} alt={product.name} class="product-image" />
        <div class="product-info">
          <h3 class="product-name">{product.name}</h3>
          <p class="product-description">{product.description}</p>
          <div class="product-price">${product.price.toFixed(2)}</div>
          <button class="buy-button" on:click={() => handleBuy(product)}>Buy Now</button>
        </div>
      </div>
    {/each}
  </div>
</div>

<style>
  h1 {
    text-align: center;
    margin: 2rem 0 1rem;
    color: #1f2937;
    font-size: 2.5rem;
  }

  .subtitle {
    text-align: center;
    font-size: 1.25rem;
    color: #6b7280;
    margin-bottom: 2rem;
  }

  .featured-products {
    margin-top: 2rem;
  }

  .featured-products h2 {
    margin-bottom: 1rem;
  }
</style>