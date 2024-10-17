<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="trxNotification">
        <h2 class="jh-entity-heading" data-cy="trxNotificationDetailsHeading">
          <span v-text="t$('monolithApp.trxNotification.detail.title')"></span> {{ trxNotification.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('monolithApp.trxNotification.recipient')"></span>
          </dt>
          <dd>
            <span>{{ trxNotification.recipient }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.trxNotification.messageType')"></span>
          </dt>
          <dd>
            <span>{{ trxNotification.messageType }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.trxNotification.content')"></span>
          </dt>
          <dd>
            <span>{{ trxNotification.content }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.trxNotification.sentAt')"></span>
          </dt>
          <dd>
            <span v-if="trxNotification.sentAt">{{ formatDateLong(trxNotification.sentAt) }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.trxNotification.customer')"></span>
          </dt>
          <dd>
            <div v-if="trxNotification.customer">
              <router-link :to="{ name: 'MstCustomerView', params: { mstCustomerId: trxNotification.customer.id } }">{{
                trxNotification.customer.id
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="trxNotification.id"
          :to="{ name: 'TrxNotificationEdit', params: { trxNotificationId: trxNotification.id } }"
          custom
          v-slot="{ navigate }"
        >
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./trx-notification-details.component.ts"></script>
