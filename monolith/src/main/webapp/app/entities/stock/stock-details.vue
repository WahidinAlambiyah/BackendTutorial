<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="stock">
        <h2 class="jh-entity-heading" data-cy="stockDetailsHeading">
          <span v-text="t$('monolithApp.stock.detail.title')"></span> {{ stock.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('monolithApp.stock.quantityAvailable')"></span>
          </dt>
          <dd>
            <span>{{ stock.quantityAvailable }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.stock.reorderLevel')"></span>
          </dt>
          <dd>
            <span>{{ stock.reorderLevel }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.stock.expiryDate')"></span>
          </dt>
          <dd>
            <span v-if="stock.expiryDate">{{ formatDateLong(stock.expiryDate) }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.stock.product')"></span>
          </dt>
          <dd>
            <div v-if="stock.product">
              <router-link :to="{ name: 'MstProductView', params: { mstProductId: stock.product.id } }">{{ stock.product.id }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link v-if="stock.id" :to="{ name: 'StockEdit', params: { stockId: stock.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./stock-details.component.ts"></script>
