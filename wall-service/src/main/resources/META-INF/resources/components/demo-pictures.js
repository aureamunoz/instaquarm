import {LitElement, html, css} from 'lit';
import '@vaadin/icon';
import {until} from 'lit/directives/until.js';
import '@vaadin/grid';
import '@vaadin/grid/vaadin-grid-sort-column.js';

export class DemoPictures extends LitElement {
    static styles = css`
      .grid {
        display: flex;
        flex-wrap: wrap;
        gap: 20px;
        padding: 10px;
        justify-content: center;
        border-radius: 5px;
        margin-top: 50px;
      }

      .card-content slot {
        display: flex;
        flex-flow: column wrap;
        padding-top: 5px;
      }

      .image::before {
        position: absolute;
        content: "";
        top: -2px;
        bottom: -2px;
        left: -2px;
        right: -2px;
        background-image: linear-gradient(312.25deg, #FFC4BC 0%,     rgba(255, 255, 255, 0) 66.19%);
        z-index: -1;
        border-radius: 18px;
      }

      .image {
        width: 350px;
        height: 350px;
        background-color: #fff;
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        box-shadow: 0 40px 80px #FBE4E0;
        border-radius: 14px;
        padding: 15px 15px 15px 15px;
        -moz-box-shadow: 0 1px 2px rgba(34,25,25,0.4);
        -webkit-box-shadow: 0 1px 3px rgba(34, 25, 25, 0.4);
        margin-left: auto;
        margin-right: auto;
      }

      .image img {
        width: 100%;
        height: 100%;
        float: left;
        -webkit-transition: opacity 1s ease-in-out;
        -moz-transition: opacity 1s ease-in-out;
        -o-transition: opacity 1s ease-in-out;
        transition: opacity 1s ease-in-out;
      }
    `;

    static properties = {
        "max": {type: Number},
        "_pictures": {state: true, type: Array}
    }


    connectedCallback() {
        super.connectedCallback();
        const prices = new EventSource("/wall/stream");
        prices.onmessage = (event) => {
            const newLength = this._pictures.unshift(JSON.parse(event.data));
            if (newLength > this.max) {
                this._pictures.pop();
            }
            console.log(this._pictures);
            this.requestUpdate();
        }

        fetch("/wall/last")
            .then(response => {
                return response.json();
            })
            .then(json => {
                this._pictures = json;
                console.log(this._pictures);
            });
    }

    render() {
        return html`${until(this._render(), html`<span>Loading drinks...</span>`)}`;
    }

    _render() {
        if (this._pictures) {
            return html`
                <div class="grid">
                    ${this._pictures.map(pic => this._renderPicture(pic))}
                </div>`;
        }
    }

    _renderPicture(pic) {
        return html`<div class="image">
            <img src="data:image/jpg;base64,${pic.picture}"/>
        </div>`
    }


}

customElements.define('demo-pictures', DemoPictures);

