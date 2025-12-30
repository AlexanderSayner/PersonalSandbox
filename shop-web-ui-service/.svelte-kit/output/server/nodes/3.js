

export const index = 3;
let component_cache;
export const component = async () => component_cache ??= (await import('../entries/pages/orders/_page.svelte.js')).default;
export const imports = ["_app/immutable/nodes/3.BFViXBpC.js","_app/immutable/chunks/scheduler.CCzmCiDb.js","_app/immutable/chunks/index.DFxhNJP8.js","_app/immutable/chunks/each.D4uxTv0x.js"];
export const stylesheets = ["_app/immutable/assets/3.chLoSyPv.css"];
export const fonts = [];
