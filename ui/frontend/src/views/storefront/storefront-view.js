import { PolymerElement } from '@polymer/polymer/polymer-element.js';
import '@vaadin/vaadin-grid/src/vaadin-grid.js';
import '@vaadin/vaadin-dialog/src/vaadin-dialog.js';
import '../../components/search-bar.js';
import '../../components/utils-mixin.js';
import './order-card.js';
import '../../../styles/shared-styles.js';
import { html } from '@polymer/polymer/lib/utils/html-tag.js';
class StorefrontView extends PolymerElement {
  static get template() {
    return html`
    <style include="shared-styles">
      :host {
        display: flex;
        flex-direction: column;
        height: 100%;
      }
    </style>

    <search-bar id="search" show-checkbox=""></search-bar>

    <vaadin-grid id="grid" theme="orders no-row-borders"></vaadin-grid>

    <vaadin-dialog id="dialog" theme="orders" on-opened-changed="_onDialogOpen"></vaadin-dialog>
`;
  }

  static get is() {
    return 'storefront-view';
  }

  ready() {
    super.ready();

    // This code is needed to measure the page load performance and can be safely removed
    // if there is no need for that.
    const grid = this.$.grid;
    const listener = () => {
      if (!grid.loading && window.performance.mark) {
        window.performance.mark('bakery-page-loaded');
        grid.removeEventListener('loading-changed', listener);
      }
    };
    grid.addEventListener('loading-changed', listener);
  }

  // Workaround for styling the dialog content https://github.com/vaadin/vaadin-dialog-flow/issues/69
  _onDialogOpen(e) {
    if (!e.detail.value) {
      return;
    }
    var content = this.$.dialog.$.overlay.content;
    content.querySelector('flow-component-renderer').setAttribute('theme', 'dialog');
  }
}

window.customElements.define(StorefrontView.is, StorefrontView);
