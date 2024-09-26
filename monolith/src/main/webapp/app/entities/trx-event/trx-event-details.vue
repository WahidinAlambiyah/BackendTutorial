<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="trxEvent">
        <h2 class="jh-entity-heading" data-cy="trxEventDetailsHeading">
          <span v-text="t$('monolithApp.trxEvent.detail.title')"></span> {{ trxEvent.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('monolithApp.trxEvent.title')"></span>
          </dt>
          <dd>
            <span>{{ trxEvent.title }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.trxEvent.description')"></span>
          </dt>
          <dd>
            <span>{{ trxEvent.description }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.trxEvent.date')"></span>
          </dt>
          <dd>
            <span v-if="trxEvent.date">{{ formatDateLong(trxEvent.date) }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.trxEvent.location')"></span>
          </dt>
          <dd>
            <span>{{ trxEvent.location }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.trxEvent.capacity')"></span>
          </dt>
          <dd>
            <span>{{ trxEvent.capacity }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.trxEvent.price')"></span>
          </dt>
          <dd>
            <span>{{ trxEvent.price }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.trxEvent.status')"></span>
          </dt>
          <dd>
            <span v-text="t$('monolithApp.EventStatus.' + trxEvent.status)"></span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.trxEvent.service')"></span>
          </dt>
          <dd>
            <div v-if="trxEvent.service">
              <router-link :to="{ name: 'MstServiceView', params: { mstServiceId: trxEvent.service.id } }">{{
                trxEvent.service.id
              }}</router-link>
            </div>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.trxEvent.testimonial')"></span>
          </dt>
          <dd>
            <div v-if="trxEvent.testimonial">
              <router-link :to="{ name: 'TrxTestimonialView', params: { trxTestimonialId: trxEvent.testimonial.id } }">{{
                trxEvent.testimonial.id
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link v-if="trxEvent.id" :to="{ name: 'TrxEventEdit', params: { trxEventId: trxEvent.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./trx-event-details.component.ts"></script>
