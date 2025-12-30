import { c as create_ssr_component, d as each, e as escape } from "../../../chunks/ssr.js";
const css = {
  code: ".orders-list.svelte-kfbsdl{margin-top:1rem}.order-card.svelte-kfbsdl{border:1px solid #e5e7eb;border-radius:0.5rem;padding:1rem;margin-bottom:1rem;background-color:#fff}.order-header.svelte-kfbsdl{display:flex;justify-content:space-between;align-items:center;margin-bottom:0.5rem}.order-status.svelte-kfbsdl{padding:0.25rem 0.5rem;border-radius:0.25rem;font-size:0.875rem;font-weight:500;margin-bottom:1rem;display:inline-block}.order-status.delivered.svelte-kfbsdl{background-color:#dcfce7;color:#166534}.order-status.processing.svelte-kfbsdl{background-color:#fef3c7;color:#92400e}.order-status.shipped.svelte-kfbsdl{background-color:#dbeafe;color:#1e40af}.order-items.svelte-kfbsdl{margin-bottom:1rem}.order-item.svelte-kfbsdl{display:flex;justify-content:space-between;padding:0.25rem 0}.order-total.svelte-kfbsdl{text-align:right;font-weight:700;font-size:1.125rem;color:#4f46e5;border-top:1px solid #e5e7eb;padding-top:0.5rem}",
  map: null
};
const Page = create_ssr_component(($$result, $$props, $$bindings, slots) => {
  let orders = [
    {
      id: 1,
      date: "2023-10-15",
      status: "Delivered",
      total: 129.99,
      items: [
        {
          name: "Wireless Headphones",
          quantity: 1,
          price: 129.99
        }
      ]
    },
    {
      id: 2,
      date: "2023-10-20",
      status: "Processing",
      total: 79.99,
      items: [
        {
          name: "Running Shoes",
          quantity: 1,
          price: 79.99
        }
      ]
    },
    {
      id: 3,
      date: "2023-10-25",
      status: "Shipped",
      total: 19.99,
      items: [
        {
          name: "Cotton T-Shirt",
          quantity: 1,
          price: 19.99
        }
      ]
    }
  ];
  $$result.css.add(css);
  return `<h2 data-svelte-h="svelte-9qysxr">My Orders</h2> <div class="orders-list svelte-kfbsdl">${each(orders, (order) => {
    return `<div class="order-card svelte-kfbsdl"><div class="order-header svelte-kfbsdl"><h3>Order #${escape(order.id)}</h3> <span class="order-date">${escape(order.date)}</span></div> <div class="${"order-status " + escape(order.status.toLowerCase(), true) + " svelte-kfbsdl"}">Status: ${escape(order.status)}</div> <div class="order-items svelte-kfbsdl">${each(order.items, (item) => {
      return `<div class="order-item svelte-kfbsdl"><span class="item-name">${escape(item.name)}</span> <span class="item-quantity">Qty: ${escape(item.quantity)}</span> <span class="item-price">$${escape(item.price.toFixed(2))}</span> </div>`;
    })}</div> <div class="order-total svelte-kfbsdl">Total: $${escape(order.total.toFixed(2))}</div> </div>`;
  })} </div>`;
});
export {
  Page as default
};
