import '@polymer/polymer/lib/elements/custom-style.js';
const $_documentContainer = document.createElement('template');

$_documentContainer.innerHTML = `<dom-module id="bakery-app-layout-theme" theme-for="vaadin-app-layout">
  <template>
    <style>
      :host {
        background-color: var(--lumo-shade-5pct) !important;
        --vaadin-app-layout-navbar-background: var(--lumo-base-color);
      }

      [part="content"] {
        height: 100%;
      }

      [part="navbar"] {
        z-index: 200;
        box-shadow: 0 0 16px 2px var(--lumo-shade-20pct);
      }

      @media (max-height: 600px) {
        :host(.hide-navbar) [part="navbar"] {
          display: none;
        }
      }

      [part="navbar"] {
        align-items: center;
        justify-content: center;
      }

      [part="navbar"]::after {
        content: '';
      }

      [part="navbar"] ::slotted(*:first-child),
      [part="navbar"]::after {
        flex: 1 0 0.001px;
      }

      @media (max-width: 800px) {
        [part="navbar"] ::slotted(vaadin-tabs) {
          max-width: 100% !important;
        }

        [part="navbar"] ::slotted(.hide-on-mobile) {
          display: none;
        }

        [part="navbar"]::after {
          content: none;
        }
      }
    </style>
  </template>
</dom-module>

<dom-module id="bakery-login-theme" theme-for="vaadin-login-overlay-wrapper">
  <template>
    <style>
      [part="brand"] {
        background-image: url(frontend/images/login-banner.jpg);
      }
    </style>
  </template>
</dom-module>

<dom-module id="bakery-crud-theme" theme-for="vaadin-crud">
  <template>
    <style>
      :host {
        height: 100%;
      }

      #new {
        display: none;
      }

      [part="toolbar"] {
        padding: 0 !important;
      }

      :host ::slotted(vaadin-grid) {
        max-width: calc(964px + var(--lumo-space-m));
        background-color: var(--lumo-base-color);
      }

      @media (min-width: 700px) {
        :host {
          flex-direction: column-reverse;
        }
      }
    </style>
  </template>
</dom-module>

<!-- Themes for dialogs -->
<dom-module id="bakery-dialog-overlay-theme" theme-for="vaadin-dialog-overlay">
  <template>
    <style>
      :host([theme~="orders"]) {
        padding: var(--lumo-space-xs);
      }

      :host([theme~="orders"]) [part="overlay"] {
        display: flex;
        animation: none !important;
        min-width: 300px;
        outline: none;
      }

      :host([theme~="crud"]) [part="content"] {
        padding: 0;
      }

      :host([theme~="orders"]) [part="content"],
      :host([theme~="orders"]) [part="overlay"] {
        max-width: 65em;
        width: 100%;
      }

      @media (max-width: 600px), (max-height: 600px) {
        :host([theme]) {
          top: 0;
          left: 0;
          right: 0;
          bottom: 0;
          padding: 0;
        }

        :host([theme]) [part="content"] {
          display: flex;
          flex-direction: column;
        }

        :host([theme]) [part="overlay"] {
          height: 100%;
          border-radius: 0 !important;
        }
      }

      /* we need explicitly set height for wrappers inside dialog-flow */
      [part="content"] ::slotted(flow-component-renderer) {
        height: 100%;
      }
    </style>
  </template>
</dom-module>

<!-- Theme for the status selector, we need to theme the text-field instead of the combo-box -->
<dom-module id="bakery-text-field-theme" theme-for="vaadin-text-field">
  <template>
    <style>
      :host([status="new"]) [part~="input-field"],
      :host([status="confirmed"]) [part~="input-field"] {
        color: var(--lumo-primary-color);
        background: var(--lumo-primary-color-10pct);
      }

      :host([status="ready"]) [part~="input-field"],
      :host([status="delivered"]) [part~="input-field"] {
        color: var(--lumo-success-color);
        background: var(--lumo-success-color-10pct);
      }

      :host([status="problem"]) [part~="input-field"],
      :host([status="cancelled"]) [part~="input-field"] {
        color: var(--lumo-error-color);
        background: var(--lumo-error-color-10pct);
      }

      :host([theme="white"]) [part~="input-field"] {
        color: var(--lumo-secondary-text-color);
        background-color: var(--lumo-base-color);
      }
    </style>
  </template>
</dom-module>

<!-- Theme for the vaadin-grid -->
<dom-module id="bakery-grid-theme" theme-for="vaadin-grid">
  <template>
    <style>
      :host {
        width: 100%;
        margin: auto;
      }

      [part~="row"]:last-child [part~="header-cell"],
      [part~="header-cell"]:not(:empty):not([details-cell]) {
        padding-top: var(--lumo-space-l);
        padding-bottom: var(--lumo-space-m);

        font-size: var(--lumo-font-size-s);
        border-bottom: 1px solid var(--lumo-shade-5pct);
      }

      :host(:not([theme~="no-row-borders"])) [part~="cell"]:not([part~="details-cell"]) {
        border-top: 1px solid var(--lumo-shade-5pct);
      }

      /* a special grid theme for the bakery storefront view */
      :host([theme~="orders"]) {
        background: transparent;
      }

      :host([theme~="orders"]) [part~="cell"]:not(:empty):not([details-cell]) {
        padding: 0;
      }

      :host([theme~="orders"]) [part~="row"][selected] [part~="cell"] {
        background: transparent !important;
      }

      :host([theme~="orders"]) [part~="body-cell"] {
        background: transparent;
      }

      @media (max-width: 600px) {
        :host([theme~="orders"]) [part~="cell"] ::slotted(vaadin-grid-cell-content) {
          padding: 0 !important;
        }
      }

      :host([theme~="dashboard"]) [part~="cell"] ::slotted(vaadin-grid-cell-content) {
        padding: 0;
      }
    </style>
  </template>
</dom-module>

<!-- shared styles for all views -->
<dom-module id="shared-styles">
  <template>
    <style>
      *,
      *::before,
      *::after,
      ::slotted(*) {
        box-sizing: border-box;
      }

      :host([hidden]),
      [hidden] {
        display: none !important;
      }

      h2,
      h3 {
        margin-top: var(--lumo-space-m);
        margin-bottom: var(--lumo-space-s);
      }

      h2 {
        font-size: var(--lumo-font-size-xxl);
      }

      h3 {
        font-size: var(--lumo-font-size-xl);
      }

      .scrollable {
        padding: var(--lumo-space-m);
        overflow: auto;
        -webkit-overflow-scrolling: touch;
      }

      .count {
        display: inline-block;
        background: var(--lumo-shade-10pct);
        border-radius: var(--lumo-border-radius);
        font-size: var(--lumo-font-size-s);
        padding: 0 var(--lumo-space-s);
        text-align: center;
      }

      .total {
        padding: 0 var(--lumo-space-s);
        font-size: var(--lumo-font-size-l);
        font-weight: bold;
        white-space: nowrap;
      }

      @media (min-width: 600px) {
        .total {
          min-width: 0;
          order: 0;
          padding: 0 var(--lumo-space-l);
        }
      }

      .flex {
        display: flex;
      }

      .flex1 {
        flex: 1 1 auto;
      }

      .bold {
        font-weight: 600;
      }

      flow-component-renderer[theme="dialog"],
      flow-component-renderer[theme="dialog"] > div {
        display: flex;
        flex-direction: column;
        flex: auto;
        height: 100%;
      }
    </style>
  </template>
</dom-module>

<custom-style>
  <style>
    @keyframes v-progress-start {
      0% {
        width: 0%;
      }

      100% {
        width: 50%;
      }
    }

    .v-loading-indicator,
    .v-system-error,
    .v-reconnect-dialog {
    	position: absolute;
    	left: 0;
    	top: 0;
    	border: none;
    	z-index: 10000;
    	pointer-events: none;
    }

    .v-system-error,
    .v-reconnect-dialog {
    	display: flex;
    	right: 0;
    	bottom: 0;
    	background: var(--lumo-shade-40pct);
    	flex-direction: column;
      align-items: center;
      justify-content: center;
      align-content: center;
    }

    .v-system-error .caption,
    .v-system-error .message,
    .v-reconnect-dialog .text {
    	width: 30em;
    	max-width: 100%;
    	padding: var(--lumo-space-xl);
    	background: var(--lumo-base-color);
    	border-radius: 4px;
    	text-align: center;
    }

    .v-system-error .caption {
    	padding-bottom: var(--lumo-space-s);
    	border-bottom-left-radius: 0;
    	border-bottom-right-radius: 0;
    }

    .v-system-error .message {
    	pointer-events: all;
    	padding-top: var(--lumo-space-s);
    	border-top-left-radius: 0;
    	border-top-right-radius: 0;
    	color: grey;
    }

    .v-loading-indicator {
    	position: fixed !important;
    	width: 50%;
    	opacity: 0.6;
    	height: 4px;
    	background: var(--lumo-primary-color);
    	transition: none;
    	animation: v-progress-start 1000ms 200ms both;
    }

    .v-loading-indicator[style*="none"] {
    	display: block !important;
    	width: 100% !important;
    	opacity: 0;
    	transition: opacity 500ms 300ms, width 300ms;
    	animation: none;
    }

    vaadin-app-layout vaadin-tab {
      font-size: var(--lumo-font-size-xs);
      padding-left: .75em;
      padding-right: .75em;
    }

    vaadin-app-layout vaadin-tab a:hover {
      text-decoration: none;
    }

    vaadin-app-layout vaadin-tab:not([selected]) a {
      color: var(--lumo-contrast-60pct);
    }

    vaadin-app-layout vaadin-tab iron-icon {
      margin: 0 4px;
      width: var(--lumo-icon-size-m);
      height: var(--lumo-icon-size-m);
      padding: .25rem;
      box-sizing: border-box !important;
    }

    vaadin-app-layout vaadin-tabs {
      max-width: 65%;
    }

    @media (min-width: 700px) {
      vaadin-app-layout vaadin-tab {
        font-size: var(--lumo-font-size-m);
        padding-left: 1em;
        padding-right: 1em;
      }
    }
  </style>
</custom-style>`;

document.head.appendChild($_documentContainer.content);
