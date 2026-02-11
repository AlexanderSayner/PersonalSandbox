<script>
  // Simulated order data
  let orders = [
    {
      id: 1,
      date: "2023-10-15",
      status: "Delivered",
      total: 129.99,
      items: [
        { name: "Wireless Headphones", quantity: 1, price: 129.99 }
      ]
    },
    {
      id: 2,
      date: "2023-10-20",
      status: "Processing",
      total: 79.99,
      items: [
        { name: "Running Shoes", quantity: 1, price: 79.99 }
      ]
    },
    {
      id: 3,
      date: "2023-10-25",
      status: "Shipped",
      total: 19.99,
      items: [
        { name: "Cotton T-Shirt", quantity: 1, price: 19.99 }
      ]
    }
  ];
</script>

<h2>My Orders</h2>

<div class="orders-list">
  {#each orders as order}
    <div class="order-card">
      <div class="order-header">
        <h3>Order #{order.id}</h3>
        <span class="order-date">{order.date}</span>
      </div>
      <div class="order-status {order.status.toLowerCase()}">
        Status: {order.status}
      </div>
      <div class="order-items">
        {#each order.items as item}
          <div class="order-item">
            <span class="item-name">{item.name}</span>
            <span class="item-quantity">Qty: {item.quantity}</span>
            <span class="item-price">${item.price.toFixed(2)}</span>
          </div>
        {/each}
      </div>
      <div class="order-total">
        Total: ${order.total.toFixed(2)}
      </div>
    </div>
  {/each}
</div>

<style>
  .orders-list {
    margin-top: 1rem;
  }

  .order-card {
    border: 1px solid #e5e7eb;
    border-radius: 0.5rem;
    padding: 1rem;
    margin-bottom: 1rem;
    background-color: #fff;
  }

  .order-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 0.5rem;
  }

  .order-status {
    padding: 0.25rem 0.5rem;
    border-radius: 0.25rem;
    font-size: 0.875rem;
    font-weight: 500;
    margin-bottom: 1rem;
    display: inline-block;
  }

  .order-status.delivered {
    background-color: #dcfce7;
    color: #166534;
  }

  .order-status.processing {
    background-color: #fef3c7;
    color: #92400e;
  }

  .order-status.shipped {
    background-color: #dbeafe;
    color: #1e40af;
  }

  .order-items {
    margin-bottom: 1rem;
  }

  .order-item {
    display: flex;
    justify-content: space-between;
    padding: 0.25rem 0;
  }

  .order-total {
    text-align: right;
    font-weight: 700;
    font-size: 1.125rem;
    color: #4f46e5;
    border-top: 1px solid #e5e7eb;
    padding-top: 0.5rem;
  }
</style>