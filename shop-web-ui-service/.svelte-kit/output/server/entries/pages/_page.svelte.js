import { c as create_ssr_component, d as each, f as add_attribute, e as escape } from "../../chunks/ssr.js";
const css = {
  code: "h1.svelte-1izw4mk.svelte-1izw4mk{text-align:center;margin:2rem 0 1rem;color:#1f2937;font-size:2.5rem}.subtitle.svelte-1izw4mk.svelte-1izw4mk{text-align:center;font-size:1.25rem;color:#6b7280;margin-bottom:2rem}.featured-products.svelte-1izw4mk.svelte-1izw4mk{margin-top:2rem}.featured-products.svelte-1izw4mk h2.svelte-1izw4mk{margin-bottom:1rem}",
  map: null
};
const Page = create_ssr_component(($$result, $$props, $$bindings, slots) => {
  let featuredProducts = [];
  $$result.css.add(css);
  return `<h1 class="svelte-1izw4mk" data-svelte-h="svelte-atpsi7">Welcome to ShopWeb</h1> <p class="subtitle svelte-1izw4mk" data-svelte-h="svelte-1qog56r">Discover amazing products at great prices</p> <div class="featured-products svelte-1izw4mk"><h2 class="svelte-1izw4mk" data-svelte-h="svelte-s8f33c">Featured Products</h2> <div class="products-grid">${each(featuredProducts, (product) => {
    return `<div class="product-card"><img${add_attribute("src", product.image, 0)}${add_attribute("alt", product.name, 0)} class="product-image"> <div class="product-info"><h3 class="product-name">${escape(product.name)}</h3> <p class="product-description">${escape(product.description)}</p> <div class="product-price">$${escape(product.price.toFixed(2))}</div> <button class="buy-button" data-svelte-h="svelte-n4e4lc">Buy Now</button></div> </div>`;
  })}</div> </div>`;
});
export {
  Page as default
};
