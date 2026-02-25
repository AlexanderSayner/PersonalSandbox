<script>
  import { onMount } from 'svelte';
  import { browser } from '$app/environment';
  import { page } from '$app/stores';

  let products = [];
  let currentPage = 1;
  let totalPages = 1;
  let selectedCategory = 'all';
  let loading = true;

  // Simulated product data
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
    },
    {
      id: 5,
      name: "Running Shoes",
      description: "Lightweight running shoes for maximum comfort",
      price: 79.99,
      category: "sports",
      image: "https://placehold.co/300x300?text=Shoes"
    },
    {
      id: 6,
      name: "Smart Watch",
      description: "Track your fitness and stay connected on the go",
      price: 199.99,
      category: "electronics",
      image: "https://placehold.co/300x300?text=Watch"
    },
    {
      id: 7,
      name: "Denim Jeans",
      description: "Classic fit denim jeans for any occasion",
      price: 59.99,
      category: "clothing",
      image: "https://placehold.co/300x300?text=Jeans"
    },
    {
      id: 8,
      name: "Cookbook",
      description: "Delicious recipes from around the world",
      price: 29.99,
      category: "books",
      image: "https://placehold.co/300x300?text=Cookbook"
    }
  ];

  // Function to filter products based on category
  function filterProducts(category) {
    if (category === 'all') {
      return mockProducts;
    }
    return mockProducts.filter(product => product.category === category);
  }

  // Function to get products for current page
  function getProductsForPage(products, page, pageSize = 4) {
    const startIndex = (page - 1) * pageSize;
    const endIndex = startIndex + pageSize;
    return products.slice(startIndex, endIndex);
  }

  // Function to calculate total pages
  function calculateTotalPages(products, pageSize = 4) {
    return Math.ceil(products.length / pageSize);
  }

  // Function to handle buy button click
  function handleBuy(product) {
    alert(`Added ${product.name} to cart!`);
    // In a real app, this would add the item to the cart store
  }

  onMount(async () => {
    if (browser) {
      // Get category from URL
      const urlParams = new URLSearchParams(window.location.search);
      selectedCategory = urlParams.get('category') || 'all';
      
      // Filter products based on selected category
      const filteredProducts = filterProducts(selectedCategory);
      
      // Calculate pagination
      totalPages = calculateTotalPages(filteredProducts);
      
      // Get products for current page
      products = getProductsForPage(filteredProducts, currentPage);
      
      loading = false;
    }
  });

  // Function to handle page change
  function changePage(page) {
    currentPage = page;
    
    // Get category from URL
    const urlParams = new URLSearchParams(window.location.search);
    selectedCategory = urlParams.get('category') || 'all';
    
    // Filter products based on selected category
    const filteredProducts = filterProducts(selectedCategory);
    
    // Update products for current page
    products = getProductsForPage(filteredProducts, currentPage);
  }

  // Handle URL changes
  $: {
    if (browser) {
      const urlParams = new URLSearchParams(window.location.search);
      const newCategory = urlParams.get('category') || 'all';
      
      if (newCategory !== selectedCategory) {
        selectedCategory = newCategory;
        currentPage = 1; // Reset to first page when category changes
        
        const filteredProducts = filterProducts(selectedCategory);
        totalPages = calculateTotalPages(filteredProducts);
        products = getProductsForPage(filteredProducts, currentPage);
      }
    }
  }
</script>

<h2>Products</h2>

{#if loading}
  <p>Loading products...</p>
{:else}
  <div class="products-grid">
    {#each products as product (product.id)}
      <div class="product-card">
        <img src={product.image} alt={product.name} class="product-image" />
        <div class="product-info">
          <h3 class="product-name">{product.name}</h3>
          <p class="product-description">{product.description}</p>
          <div class="product-price">${product.price.toFixed(2)}</div>
          <button class="buy-button" on:click={() => handleBuy(product)}>Buy Now</button>
        </div>
      </div>
    {:else}
      <p>No products found in this category.</p>
    {/each}
  </div>

  {#if totalPages > 1}
    <div class="pagination">
      {#each Array.from({ length: totalPages }, (_, i) => i + 1) as pageNum}
        <button 
          class="pagination-button {currentPage === pageNum ? 'active' : ''}" 
          on:click={() => changePage(pageNum)}
        >
          {pageNum}
        </button>
      {/each}
    </div>
  {/if}
{/if}